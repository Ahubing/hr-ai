package com.open.ai.eros.common.vo;

import java.util.Objects;

/**
 * @类名：ToolExecutionRequest
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/20 19:26
 */

public class ToolExecutionRequest {

    private final String id;
    private final String name;
    private final String arguments;

    private ToolExecutionRequest(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.arguments = builder.arguments;
    }

    public String id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public String arguments() {
        return this.arguments;
    }

    public boolean equals(Object another) {
        if (this == another) {
            return true;
        } else {
            return another instanceof ToolExecutionRequest && this.equalTo((ToolExecutionRequest)another);
        }
    }

    @Override
    public String toString() {
        return "ToolExecutionRequest{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", arguments='" + arguments + '\'' +
                '}';
    }

    private boolean equalTo(ToolExecutionRequest another) {
        return Objects.equals(this.id, another.id) && Objects.equals(this.name, another.name) && Objects.equals(this.arguments, another.arguments);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(this.id);
        h += (h << 5) + Objects.hashCode(this.name);
        h += (h << 5) + Objects.hashCode(this.arguments);
        return h;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;
        private String name;
        private String arguments;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder arguments(String arguments) {
            this.arguments = arguments;
            return this;
        }

        public ToolExecutionRequest build() {
            return new ToolExecutionRequest(this);
        }
    }
}
