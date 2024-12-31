/*
 * Copyright (c) 2024 LangChat. TyCoding All Rights Reserved.
 *
 * Licensed under the GNU Affero General Public License, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/agpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.open.ai.eros.ai.lang.chain.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 *     pgvector:
 *       host: 127.0.0.1
 *       user: root
 *       password: root
 *       port: 5432
 *       database: langchat
 *       table: langchat_store
 *       dimension: 384
 */

@Data
@Accessors(chain = true)
@Configuration
@ConfigurationProperties(
        "langchain4j.pgvector"
)
public class PgVectorStoreProperties {

    private String host;
    private Integer port;
    private String user;
    private String password;
    private String database;
    private String table;
    private Integer dimension;
    private Boolean useIndex;
    private Integer indexListSize;
    private Boolean createTable = true;
    private Boolean dropTableFirst = false;
}
