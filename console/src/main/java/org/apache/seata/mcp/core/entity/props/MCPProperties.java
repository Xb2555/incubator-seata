/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.seata.mcp.core.entity.props;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MCPProperties {

    public static final String SSE_TYPE = "sse";
    public static final String STREAMABLE_TYPE = "streamable";

    private boolean enableAuth = true;

    private Long queryDuration = 86400000L;

    private String mcpType = SSE_TYPE;

    private StreamableProperties streamableProperties;

    private SseServerProperties sseServerProperties;

    public MCPProperties() {}

    public boolean isSseType() {
        return mcpType.equals(SSE_TYPE);
    }

    public List<String> getEndpoints() {
        List<String> result = new ArrayList<>();
        if (isSseType()) {
            result.add(sseServerProperties.sseEndpoint);
            result.add(sseServerProperties.messageEndpoint);
        } else {
            result.add(streamableProperties.mcpEndPoint);
        }
        return result;
    }

    public static class StreamableProperties {
        private String mcpEndPoint = "/mcp";

        public String getMcpEndPoint() {
            return mcpEndPoint;
        }

        public void setMcpEndPoint(String mcpEndPoint) {
            this.mcpEndPoint = mcpEndPoint;
        }

        public StreamableProperties() {}

        public StreamableProperties(String mcpEndPoint) {
            this.mcpEndPoint = mcpEndPoint;
        }
    }

    public static class SseServerProperties {

        private String sseEndpoint = "/sse";

        private String messageEndpoint = "/message";

        public String getSseEndpoint() {
            return sseEndpoint;
        }

        public void setSseEndpoint(String sseEndpoint) {
            this.sseEndpoint = sseEndpoint;
        }

        public String getMessageEndpoint() {
            return messageEndpoint;
        }

        public void setMessageEndpoint(String messageEndpoint) {
            this.messageEndpoint = messageEndpoint;
        }

        public SseServerProperties() {}

        public SseServerProperties(String sseEndpoint, String messageEndpoint) {
            this.sseEndpoint = sseEndpoint;
            this.messageEndpoint = messageEndpoint;
        }
    }

    @Autowired
    private Environment env;

    @PostConstruct
    public void init() {
        mcpType = env.getProperty("spring.ai.mcp.server.protocol", "sse");
        if (mcpType.equals(STREAMABLE_TYPE)) {
            String mcpEndPoint = env.getProperty("spring.ai.mcp.server.streamable-http.mcp-endpoint", "/mcp");
            streamableProperties = new StreamableProperties(mcpEndPoint);
        } else {
            mcpType = SSE_TYPE;
            String sseEndpoint = env.getProperty("spring.ai.mcp.server.sse-endpoint", "/sse");
            String messageEndpoint = env.getProperty("spring.ai.mcp.server.sse-message-endpoint", "/mcp/message");
            sseServerProperties = new SseServerProperties(sseEndpoint, messageEndpoint);
        }
        queryDuration = Long.parseLong(env.getProperty("seata.mcp.query.max_query_duration", "604800000"));
        enableAuth = Boolean.parseBoolean(env.getProperty("seata.mcp.auth.enabled", "true"));
    }

    public Long getQueryDuration() {
        return queryDuration;
    }

    public void setQueryDuration(Long queryDuration) {
        this.queryDuration = queryDuration;
    }

    public boolean isEnableAuth() {
        return enableAuth;
    }

    public void setEnableAuth(boolean enableAuth) {
        this.enableAuth = enableAuth;
    }

    public String getMcpType() {
        return mcpType;
    }

    public Environment getEnv() {
        return env;
    }

    public StreamableProperties getStreamableProperties() {
        return streamableProperties;
    }

    public SseServerProperties getSseServerProperties() {
        return sseServerProperties;
    }

    public void setMcpType(String mcpType) {
        this.mcpType = mcpType;
    }

    public void setStreamableProperties(StreamableProperties streamableProperties) {
        this.streamableProperties = streamableProperties;
    }

    public void setSseServerProperties(SseServerProperties sseServerProperties) {
        this.sseServerProperties = sseServerProperties;
    }
}
