/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.alibaba.rocketmq.store.transaction.jdbc;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.common.constant.LoggerName;
import com.alibaba.rocketmq.store.transaction.TransactionRecord;
import com.alibaba.rocketmq.store.transaction.TransactionStore;
import com.mchange.v2.c3p0.ComboPooledDataSource;


/**
 * 目前的逻辑里面，本类中的sql操作方法均是在单线程环境下运行，因此不存在线程安全问题，故而数据库连接池活跃数量配置为2，方便备用
 * @author Administrator
 *
 */
public class JDBCTransactionStore implements TransactionStore {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.TransactionLoggerName);
    private static ComboPooledDataSource dataSource;
    private final JDBCTransactionStoreConfig jdbcTransactionStoreConfig;
    private AtomicLong totalRecordsValue = new AtomicLong(0);
    private String brokerName;
    private String tableName; //每个主节点broker各自创建一张独立的事务表，因为offset只是在broker内部唯一
    private String insertSql;
    private String deleteSql;
    private String selectSql;
    private String countSql;
    
    public JDBCTransactionStore(JDBCTransactionStoreConfig jdbcTransactionStoreConfig, String brokerName) {
        this.jdbcTransactionStoreConfig = jdbcTransactionStoreConfig;
    	this.brokerName = brokerName;
    	this.tableName = "rocketmq_" + brokerName + "_transaction";
    	this.tableName = this.tableName.replaceAll("-", "_"); // 这里特别注意两个主节点的名称不能重复，并且不能有-、_这样的混淆，这样也会重复的，导致一些不可预测的问题发生
    	this.insertSql = "insert into " + this.tableName + " values (?, ?, ?)";
    	this.deleteSql = "delete from " + this.tableName + " where offset = ?";
    	this.selectSql = "select * from " + this.tableName + " where createTime < NOW() - INTERVAL 30 SECOND";
    	this.countSql = "select count(offset) as total from " + this.tableName;
    }

    private Connection getConnection() throws SQLException{
    	return dataSource.getConnection();
    }
    
    @Override
    public boolean open() {
        loadDriver();
        
        if (!this.computeTotalRecords()) {
            return this.createDB();
        }
        
        return true;
    }
    
    private void loadDriver() {
    	try {
    		dataSource = new ComboPooledDataSource();
			dataSource.setDriverClass(jdbcTransactionStoreConfig.getJdbcDriverClass());
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			throw new RuntimeException("jdbc数据源初始化失败", e);
		}
		dataSource.setJdbcUrl(jdbcTransactionStoreConfig.getJdbcURL());
		dataSource.setUser(jdbcTransactionStoreConfig.getJdbcUser());
		dataSource.setPassword(jdbcTransactionStoreConfig.getJdbcPassword());
		dataSource.setMaxPoolSize(jdbcTransactionStoreConfig.getMaxPoolSize());
		dataSource.setMinPoolSize(jdbcTransactionStoreConfig.getMinPoolSize());
		dataSource.setInitialPoolSize(jdbcTransactionStoreConfig.getInitialPoolSize());
		dataSource.setAcquireIncrement(jdbcTransactionStoreConfig.getAcquireIncrement());
		dataSource.setAutomaticTestTable(jdbcTransactionStoreConfig.getAutomaticTestTable());
    }
    
    private boolean computeTotalRecords() {
        Statement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        try {
        	connection = getConnection();
        	connection.setAutoCommit(true);
        	statement = connection.createStatement();
            resultSet = statement.executeQuery(this.countSql);
            if (resultSet.next()) {
            	this.totalRecordsValue.set(resultSet.getLong(1));
            }
            
            return true;
        } catch (Exception e) {
            log.error("computeTotalRecords Exception", e);
            return false;
        } finally {
            if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
                }
            }
            
            if (null != resultSet) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                }
            }
            
            if(connection != null){
            	try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
            }
        }
    }

    private boolean createDB() {
        Statement statement = null;
        Connection connection = null;
        try {
        	connection = getConnection();
        	connection.setAutoCommit(true);
            statement = connection.createStatement();
            String sql = this.createTableSql();
            log.info("createDB SQL:\n {}", sql);
            statement.execute(sql);
            return true;
        } catch (Exception e) {
            log.error("createDB Exception", e);
            return false;
        } finally {
            if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    log.warn("Close statement exception", e);
                }
            }
            
            if (null != connection) {
                try {
                	connection.close();
                } catch (SQLException e) {
                    log.warn("Close connection exception", e);
                }
            }
        }
    }

    private String createTableSql() {
        String fileContent = "CREATE TABLE " + this.tableName + " ( `offset` bigint(20) NOT NULL, `producerGroup` varchar(64) DEFAULT NULL, `createTime` datetime DEFAULT NULL, PRIMARY KEY (`offset`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8";
        return fileContent;
    }
    
    @Override
    public void close() {
    	if(dataSource != null){
        	dataSource.close();
    	}
    }
    
    @Override
    public boolean put(final TransactionRecord transactionRecord) {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
        	connection = getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(this.insertSql);
            statement.setLong(1, transactionRecord.getOffset());
            statement.setString(2, transactionRecord.getProducerGroup());
            statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            int num = statement.executeUpdate();
            connection.commit();
            this.totalRecordsValue.addAndGet(num);
            return true;
        } catch (Exception e) {
            log.error("put transactionRecord Exception", e);
            return false;
        } finally {
            if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    log.warn("Close statement exception", e);
                }
            }
            
            if (null != connection) {
                try {
                	connection.close();
                } catch (SQLException e) {
                    log.warn("Close connection exception", e);
                }
            }
        }
    }

    @Override
    public boolean put(final List<TransactionRecord> trs) {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
        	connection = getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(this.insertSql);
            for (TransactionRecord tr : trs) {
                statement.setLong(1, tr.getOffset());
                statement.setString(2, tr.getProducerGroup());
                statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                statement.addBatch();
            }
            int[] executeBatch = statement.executeBatch();
            connection.commit();
            this.totalRecordsValue.addAndGet(updatedRows(executeBatch));
            return true;
        } catch (Exception e) {
            log.error("put transactionRecord list Exception", e);
            return false;
        } finally {
            if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    log.warn("Close statement exception", e);
                }
            }
            
            if (null != connection) {
                try {
                	connection.close();
                } catch (SQLException e) {
                    log.warn("Close connection exception", e);
                }
            }
        }
    }
    
    private long updatedRows(int[] rows) {
        long res = 0;
        for (int i : rows) {
            res += i;
        }

        return res;
    }

    @Override
    public void remove(final Long offset) {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
        	connection = getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(deleteSql);
            statement.setLong(1, offset);
            int num = statement.executeUpdate();
            connection.commit();
            this.totalRecordsValue.addAndGet(0 - num);
        } catch (Exception e) {
            log.error("remove by offset Exception", e);
        } finally {
            if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
                }
            }
            
            if (null != connection) {
                try {
                	connection.close();
                } catch (SQLException e) {
                    log.warn("Close connection exception", e);
                }
            }
        }
    }
    
    @Override
    public void remove(final List<Long> pks) {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
        	connection = getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(deleteSql);
            for (long pk : pks) {
                statement.setLong(1, pk);
                statement.addBatch();
            }
            int[] executeBatch = statement.executeBatch();
            connection.commit();
            this.totalRecordsValue.addAndGet(0 - updatedRows(executeBatch));
        } catch (Exception e) {
            log.error("remove by offset list Exception", e);
        } finally {
            if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
                }
            }
            
            if (null != connection) {
                try {
                	connection.close();
                } catch (SQLException e) {
                    log.warn("Close connection exception", e);
                }
            }
        }
    }

    @Override
    public List<TransactionRecord> traverse(long pk, int nums) {
        return null;
    }

    @Override
    public long totalRecords() {
        return this.totalRecordsValue.get();
    }

    @Override
    public long minPK() {
        return 0;
    }

    @Override
    public long maxPK() {
        return 0;
    }
    
    public Map<String, Set<Long>> getLaggedTransaction() {
        Statement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        try {
        	connection = getConnection();
        	connection.setAutoCommit(true);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(this.selectSql);
            Map<String, Set<Long>> result = new HashMap<String, Set<Long>>();
            while (resultSet.next()) {
                String producerGroup = resultSet.getString("producerGroup");
                if (result.containsKey(producerGroup)) {
                    result.get(producerGroup).add(resultSet.getLong("offset"));
                } else {
                    Set<Long> offsets = new HashSet<Long>();
                    result.put(producerGroup, offsets);
                    offsets.add(resultSet.getLong("offset"));
                }
            }
            
            return result;
        } catch (SQLException e) {
            log.error("Failed to getLaggedTransaction statement.", e);
        } finally {
        	if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    log.warn("Close statement exception", e);
                }
            }
        	
        	if (null != resultSet) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                	log.warn("Close resultSet exception", e);
                }
            }
        	
        	if (null != connection) {
                try {
                	connection.close();
                } catch (SQLException e) {
                    log.warn("Close connection exception", e);
                }
            }
        }
        
        return null;
    }
}