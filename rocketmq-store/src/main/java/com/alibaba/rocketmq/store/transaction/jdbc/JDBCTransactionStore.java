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

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.constant.LoggerName;
import com.alibaba.rocketmq.store.transaction.TransactionRecord;
import com.alibaba.rocketmq.store.transaction.TransactionStore;


public class JDBCTransactionStore implements TransactionStore {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.TransactionLoggerName);
    private final JDBCTransactionStoreConfig jdbcTransactionStoreConfig;
    private Connection connection;
    private AtomicLong totalRecordsValue = new AtomicLong(0);


    public JDBCTransactionStore(JDBCTransactionStoreConfig jdbcTransactionStoreConfig) {
        this.jdbcTransactionStoreConfig = jdbcTransactionStoreConfig;
    }

    @Override
    public boolean open() {
        if (this.loadDriver()) {
            Properties props = new Properties();
            props.put("user", jdbcTransactionStoreConfig.getJdbcUser());
            props.put("password", jdbcTransactionStoreConfig.getJdbcPassword());

            try {
                this.connection =
                        DriverManager.getConnection(this.jdbcTransactionStoreConfig.getJdbcURL(), props);
                
                if (!this.computeTotalRecords()) {
                    return this.createDB();
                }

                return true;
            } catch (SQLException e) {
                log.info("Create JDBC Connection Exeption", e);
            }
        }

        return false;
    }

    private boolean loadDriver() {
        try {
            Class.forName(this.jdbcTransactionStoreConfig.getJdbcDriverClass()).newInstance();
            log.info("Loaded the appropriate driver, {}",
                    this.jdbcTransactionStoreConfig.getJdbcDriverClass());
            return true;
        } catch (Exception e) {
            log.info("Loaded the appropriate driver Exception", e);
        }

        return false;
    }

    private boolean computeTotalRecords() {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = this.connection.createStatement();
            resultSet = statement.executeQuery("select count(offset) as total from t_transaction");
            if (!resultSet.next()) {
                log.warn("computeTotalRecords ResultSet is empty");
                return false;
            }
            
            this.totalRecordsValue.set(resultSet.getLong(1));
        } catch (Exception e) {
            log.warn("computeTotalRecords Exception", e);
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
        }

        return true;
    }

    private boolean createDB() {
        Statement statement = null;
        try {
            this.connection.setAutoCommit(false);
            statement = this.connection.createStatement();
            String sql = this.createTableSql();
            log.info("createDB SQL:\n {}", sql);
            statement.execute(sql);
            this.connection.commit();
            return true;
        } catch (Exception e) {
            log.warn("createDB Exception", e);
            return false;
        } finally {
            if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    log.warn("Close statement exception", e);
                }
            }
        }
    }

    private String createTableSql() {
        URL resource = JDBCTransactionStore.class.getClassLoader().getResource("transaction.sql");
        String fileContent = MixAll.file2String(resource);
        return fileContent;
    }

    @Override
    public void close() {
        try {
            if (this.connection != null) {
                this.connection.close();
            }
        } catch (SQLException e) {
        }
    }
    
    @Override
    public boolean put(final TransactionRecord transactionRecord) {
        PreparedStatement statement = null;
        try {
            this.connection.setAutoCommit(false);
            statement = this.connection.prepareStatement("insert into t_transaction values (?, ?, ?)");
            statement.setLong(1, transactionRecord.getOffset());
            statement.setString(2, transactionRecord.getProducerGroup());
            statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            int num = statement.executeUpdate();
            this.connection.commit();
            this.totalRecordsValue.addAndGet(num);
            return true;
        } catch (Exception e) {
            log.warn("createDB Exception", e);
            return false;
        } finally {
            if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    log.warn("Close statement exception", e);
                }
            }
        }
    }

    @Override
    public boolean put(final List<TransactionRecord> trs) {
        PreparedStatement statement = null;
        try {
            this.connection.setAutoCommit(false);
            statement = this.connection.prepareStatement("insert into t_transaction values (?, ?, ?)");
            for (TransactionRecord tr : trs) {
                statement.setLong(1, tr.getOffset());
                statement.setString(2, tr.getProducerGroup());
                statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                statement.addBatch();
            }
            int[] executeBatch = statement.executeBatch();
            this.connection.commit();
            this.totalRecordsValue.addAndGet(updatedRows(executeBatch));
            return true;
        } catch (Exception e) {
            log.warn("createDB Exception", e);
            return false;
        } finally {
            if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    log.warn("Close statement exception", e);
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
        try {
            this.connection.setAutoCommit(false);
            statement = this.connection.prepareStatement("DELETE FROM t_transaction WHERE offset = ?");
            statement.setLong(1, offset);
            int num = statement.executeUpdate();
//            System.out.println(num);
            this.connection.commit();
            this.totalRecordsValue.addAndGet(0 - num);
        } catch (Exception e) {
            log.warn("remove by offset Exception", e);
        } finally {
            if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
                }
            }
        }
    }
    
    @Override
    public void remove(final List<Long> pks) {
        PreparedStatement statement = null;
        try {
            this.connection.setAutoCommit(false);
            statement = this.connection.prepareStatement("DELETE FROM t_transaction WHERE offset = ?");
            for (long pk : pks) {
                statement.setLong(1, pk);
                statement.addBatch();
            }
            int[] executeBatch = statement.executeBatch();
//            System.out.println(Arrays.toString(executeBatch));
            this.connection.commit();
            this.totalRecordsValue.addAndGet(0 - updatedRows(executeBatch));
        } catch (Exception e) {
            log.warn("createDB Exception", e);
        } finally {
            if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
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
        if (null != connection) {
            Statement statement = null;
            ResultSet resultSet = null;
            try {
            	connection.setAutoCommit(true);
                statement = connection.createStatement();
                resultSet = statement.executeQuery("SELECT * FROM t_transaction WHERE createTime < NOW() - INTERVAL 30 SECOND");
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
            }
        }
        
        return null;
    }
}
