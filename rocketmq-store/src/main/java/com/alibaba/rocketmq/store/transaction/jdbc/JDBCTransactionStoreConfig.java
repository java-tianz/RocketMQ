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

public class JDBCTransactionStoreConfig {
    private String jdbcDriverClass = "com.mysql.jdbc.Driver";
    private String jdbcURL = "jdbc:mysql://192.168.0.134:3306/lottotvdev?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true";
    private String jdbcUser = "kaifa";
    private String jdbcPassword = "123456";
    private int maxPoolSize = 10; // <!--连接池中保留的最大连接数。默认值: 15 --> 
    private int minPoolSize = 2; // <!-- 连接池中保留的最小连接数，默认为：3-->
    private int initialPoolSize = 2; //  <!-- 初始化连接池中的连接数，取值应在minPoolSize与maxPoolSize之间，默认为3-->  
    private int acquireIncrement = 5; // <!--当连接池中的连接耗尽的时候c3p0一次同时获取的连接数。默认值: 3 --> 
    private String automaticTestTable = "db_connection_test"; //<!--c3p0将建一张名为Test的空表，并使用其自带的查询语句进行测试。如果定义了这个参数那么属性preferredTestQuery将被忽略。你不能在这张Test表上进行任何操作，它将只供c3p0测试使用。默认值: null -->
    
    public String getJdbcDriverClass() {
        return jdbcDriverClass;
    }
    
    public void setJdbcDriverClass(String jdbcDriverClass) {
        this.jdbcDriverClass = jdbcDriverClass;
    }
    
    public String getJdbcURL() {
        return jdbcURL;
    }
    
    public void setJdbcURL(String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }
    
    public String getJdbcUser() {
        return jdbcUser;
    }
    
    public void setJdbcUser(String jdbcUser) {
        this.jdbcUser = jdbcUser;
    }
    
    public String getJdbcPassword() {
        return jdbcPassword;
    }
    
    public void setJdbcPassword(String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
    }

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public int getMinPoolSize() {
		return minPoolSize;
	}

	public void setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}

	public int getInitialPoolSize() {
		return initialPoolSize;
	}

	public void setInitialPoolSize(int initialPoolSize) {
		this.initialPoolSize = initialPoolSize;
	}

	public int getAcquireIncrement() {
		return acquireIncrement;
	}

	public void setAcquireIncrement(int acquireIncrement) {
		this.acquireIncrement = acquireIncrement;
	}

	public String getAutomaticTestTable() {
		return automaticTestTable;
	}

	public void setAutomaticTestTable(String automaticTestTable) {
		this.automaticTestTable = automaticTestTable;
	}
	
}