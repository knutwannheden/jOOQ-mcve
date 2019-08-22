/*
 * This work is dual-licensed
 * - under the Apache Software License 2.0 (the "ASL")
 * - under the jOOQ License and Maintenance Agreement (the "jOOQ License")
 * =============================================================================
 * You may choose which license applies to you:
 *
 * - If you're using this work with Open Source databases, you may choose
 *   either ASL or jOOQ License.
 * - If you're using this work with at least one commercial database, you must
 *   choose jOOQ License
 *
 * For more information, please visit http://www.jooq.org/licenses
 *
 * Apache Software License 2.0:
 * -----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * jOOQ License and Maintenance Agreement:
 * -----------------------------------------------------------------------------
 * Data Geekery grants the Customer the non-exclusive, timely limited and
 * non-transferable license to install and use the Software under the terms of
 * the jOOQ License and Maintenance Agreement.
 *
 * This library is distributed with a LIMITED WARRANTY. See the jOOQ License
 * and Maintenance Agreement for more details: http://www.jooq.org/licensing
 */
package org.jooq.mcve.test;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.codegen.GenerationTool;
import org.jooq.impl.DSL;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Property;
import org.jooq.meta.jaxb.Target;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;

public class MCVETest {
    
    @Rule
    public PostgreSQLContainer<?> db = new PostgreSQLContainer<>().withInitScript("db/init.sql");

    Connection connection;
    DSLContext ctx;

    @Before
    public void setup() throws Exception {
        connection = db.createConnection("");
        ctx = DSL.using(connection);
    }

    @After
    public void after() throws Exception {
        ctx = null;
        connection.close();
        connection = null;
    }

    @Test
    public void mcveTest() throws Exception {
        new GenerationTool().run(new Configuration()
            .withJdbc(new Jdbc()
                .withUrl(db.getJdbcUrl())
                .withDriver("org.postgresql.Driver")
                .withUser(db.getUsername())
                .withPassword(db.getPassword())
            )
            .withGenerator(new Generator()
                .withName("org.jooq.codegen.XMLGenerator")
                .withDatabase(new Database()
                    .withInputSchema("public"))
                .withTarget(new Target()
                    .withDirectory("target/generated-sources/jooq-mcve")
                    .withPackageName("org.jooq.mcve")
                )
            )
        );

        new GenerationTool().run(new Configuration()
            .withGenerator(new Generator()
                .withName("org.jooq.codegen.JavaGenerator")
                .withDatabase(new Database()
                    .withName("org.jooq.meta.xml.XMLDatabase")
                    .withProperties(
                        new Property().withKey("dialect").withValue("POSTGRES"),
                        new Property().withKey("xml-file").withValue("target/generated-sources/jooq-mcve/org/jooq/mcve/information_schema.xml")
                    )
                )
                .withTarget(new Target()
                    .withDirectory("target/generated-sources/jooq-mcve")
                    .withPackageName("org.jooq.mcve")
                )
            )
        );
    }
}
