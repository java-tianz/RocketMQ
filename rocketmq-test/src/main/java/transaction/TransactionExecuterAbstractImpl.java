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
package transaction;

import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.common.message.Message;

public abstract class TransactionExecuterAbstractImpl implements LocalTransactionExecuter {

    @Override
    public LocalTransactionState executeLocalTransactionBranch(final Message msg, final Object arg) {
		doLocalTransactionBranchCode(); // 执行业务代码
    	
    	saveLocalTransactionBranchState(); //保存本地事务状态
    	
    	return LocalTransactionState.COMMIT_MESSAGE;
    	
    	
//        int value = transactionIndex.getAndIncrement();
//        
//        System.out.println("value: " + value);
//        if (value == 0) {
//        	 System.out.println("本地事务分支异常");
//            throw new RuntimeException("Could not find db");
//        } else if ((value % 5) == 0) {
//        	System.out.println("消息回滚。。。");
//            return LocalTransactionState.ROLLBACK_MESSAGE;
//        } else if ((value % 4) == 0) {
//        	System.out.println("消息提交。。。");
//            return LocalTransactionState.COMMIT_MESSAGE;
//        }
//
//        System.out.println("消息未知状态。。。");
//        return LocalTransactionState.UNKNOW;
    }

    protected abstract void saveLocalTransactionBranchState();

	protected void doLocalTransactionBranchCode() {
		
	}
}
