/* Copyright 2005 Brian McCallister
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.skife.csv;

/**
 * Used with {@link SimpleWriter#write(java.io.File, org.skife.csv.WriterCallback)}
 * or {@link SimpleWriter#append(Object[])} to allow the library to
 * handle resource cleanup.
 */
public interface WriterCallback
{
    /**
     * Will be passed an open CSVWriter
     * @throws Exception which will be wrapped in an RuntimeException
     */
    void withWriter(SimpleWriter writer) throws Exception;
}
