/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.julongchain.common.exception;

/**
 * JulongChain异常父类
 *
 * @author zhouhui
 * @date 2018/3/29
 * @company Dingxuan
 */
public class JulongChainException extends Exception {
    public JulongChainException() {
        super();
    }

    public JulongChainException(String message) {
        super(message);
    }

    public JulongChainException(String message, Throwable cause) {
        super(message, cause);
    }


    public JulongChainException(Throwable cause) {
        super(cause);
    }


    protected JulongChainException(String message, Throwable cause,
                                   boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
