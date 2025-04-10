package com.open.ai.eros.ai.lang.chain.service;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.exception.IllegalConfigurationException;
import dev.langchain4j.internal.Exceptions;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.Capability;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.model.input.structured.StructuredPromptProcessor;
import dev.langchain4j.model.moderation.Moderation;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.rag.AugmentationRequest;
import dev.langchain4j.rag.AugmentationResult;
import dev.langchain4j.rag.query.Metadata;
import dev.langchain4j.service.*;
import dev.langchain4j.service.output.JsonSchemas;
import dev.langchain4j.service.output.ServiceOutputParser;
import dev.langchain4j.service.tool.ToolExecution;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.service.tool.ToolProviderRequest;
import dev.langchain4j.service.tool.ToolProviderResult;
import dev.langchain4j.spi.ServiceHelper;
import dev.langchain4j.spi.services.TokenStreamAdapter;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NewDefaultAiServices<T> extends NewAiServices<T> {
    private static final int MAX_SEQUENTIAL_TOOL_EXECUTIONS = 100;
    private final ServiceOutputParser serviceOutputParser = new ServiceOutputParser();
    private final Collection<TokenStreamAdapter> tokenStreamAdapters = ServiceHelper.loadFactories(TokenStreamAdapter.class);
    NewDefaultAiServices(AiServiceContext context) {
        super(context);
    }

    static void validateParameters(Method method) {
        Parameter[] parameters = method.getParameters();
        if (parameters != null && parameters.length >= 2) {
            Parameter[] var2 = parameters;
            int var3 = parameters.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Parameter parameter = var2[var4];
                V v = (V)parameter.getAnnotation(V.class);
                UserMessage userMessage = (UserMessage)parameter.getAnnotation(UserMessage.class);
                MemoryId memoryId = (MemoryId)parameter.getAnnotation(MemoryId.class);
                UserName userName = (UserName)parameter.getAnnotation(UserName.class);
                if (v == null && userMessage == null && memoryId == null && userName == null) {
                    throw IllegalConfigurationException.illegalConfiguration("Parameter '%s' of method '%s' should be annotated with @V or @UserMessage or @UserName or @MemoryId", new Object[]{parameter.getName(), method.getName()});
                }
            }

        }
    }

    public T build() {
        this.performBasicValidation();
        Method[] var1 = this.context.aiServiceClass.getMethods();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Method method = var1[var3];
            if (method.isAnnotationPresent(Moderate.class) && this.context.moderationModel == null) {
                throw IllegalConfigurationException.illegalConfiguration("The @Moderate annotation is present, but the moderationModel is not set up. Please ensure a valid moderationModel is configured before using the @Moderate annotation.");
            }

            if (method.getReturnType() == Result.class || method.getReturnType() == List.class || method.getReturnType() == Set.class) {
                TypeUtils.validateReturnTypesAreProperlyParametrized(method.getName(), method.getGenericReturnType());
            }
        }

        Object o = Proxy.newProxyInstance(this.context.aiServiceClass.getClassLoader(), new Class[]{this.context.aiServiceClass}, new InvocationHandler() {
            private final ExecutorService executor = Executors.newCachedThreadPool();

            public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
                if (method.getDeclaringClass() == Object.class) {
                    return method.invoke(this, args);
                } else {
                    NewDefaultAiServices.validateParameters(method);
                    Object memoryId = NewDefaultAiServices.findMemoryId(method, args).orElse("default");
                    Optional<SystemMessage> systemMessage = NewDefaultAiServices.this.prepareSystemMessage(memoryId, method, args);
                    dev.langchain4j.data.message.UserMessage userMessage = NewDefaultAiServices.prepareUserMessage(method, args);
                    AugmentationResult augmentationResult = null;
                    if (NewDefaultAiServices.this.context.retrievalAugmentor != null) {
                        List<ChatMessage> chatMemory = NewDefaultAiServices.this.context.hasChatMemory() ? NewDefaultAiServices.this.context.chatMemory(memoryId).messages() : null;
                        Metadata metadata = Metadata.from(userMessage, memoryId, chatMemory);
                        AugmentationRequest augmentationRequest = new AugmentationRequest(userMessage, metadata);
                        augmentationResult = NewDefaultAiServices.this.context.retrievalAugmentor.augment(augmentationRequest);
                        userMessage = (dev.langchain4j.data.message.UserMessage) augmentationResult.chatMessage();
                    }

                    Type returnType = method.getGenericReturnType();
                    boolean streaming = returnType == TokenStream.class || this.canAdaptTokenStreamTo(returnType);
                    boolean supportsJsonSchema = this.supportsJsonSchema();
                    Optional<JsonSchema> jsonSchema = Optional.empty();
                    if (supportsJsonSchema && !streaming) {
                        jsonSchema = JsonSchemas.jsonSchemaFrom(returnType);
                    }

                    if ((!supportsJsonSchema || !jsonSchema.isPresent()) && !streaming) {
                        userMessage = this.appendOutputFormatInstructions(returnType, userMessage);
                    }

                    if (NewDefaultAiServices.this.context.hasChatMemory()) {
                        ChatMemory chatMemoryx = NewDefaultAiServices.this.context.chatMemory(memoryId);
                        Objects.requireNonNull(chatMemoryx);
                        systemMessage.ifPresent(chatMemoryx::add);
                        chatMemoryx.add(userMessage);
                    }

                    List<Object> messages;
                    if (NewDefaultAiServices.this.context.hasChatMemory()) {
                        messages = Collections.singletonList(NewDefaultAiServices.this.context.chatMemory(memoryId).messages());
                    } else {
                        messages = new ArrayList();
                        Objects.requireNonNull(messages);
                        systemMessage.ifPresent(messages::add);
                        messages.add(userMessage);
                    }

                    Future<Moderation> moderationFuture = this.triggerModerationIfNeeded(method, (List) messages);
                    List<ToolSpecification> toolSpecifications = NewDefaultAiServices.this.context.toolSpecifications;
                    Map<String, ToolExecutor> toolExecutors = NewDefaultAiServices.this.context.toolExecutors;
                    if (NewDefaultAiServices.this.context.toolProvider != null) {
                        toolSpecifications = new ArrayList();
                        toolExecutors = new HashMap();
                        ToolProviderRequest toolProviderRequest = new ToolProviderRequest(memoryId, userMessage);
                        ToolProviderResult toolProviderResult = NewDefaultAiServices.this.context.toolProvider.provideTools(toolProviderRequest);
                        if (toolProviderResult != null) {
                            Map<ToolSpecification, ToolExecutor> tools = toolProviderResult.tools();
                            Iterator var19 = tools.keySet().iterator();

                            while (var19.hasNext()) {
                                ToolSpecification toolSpecification = (ToolSpecification) var19.next();
                                ((List) toolSpecifications).add(toolSpecification);
                                ((Map) toolExecutors).put(toolSpecification.name(), (ToolExecutor) tools.get(toolSpecification));
                            }
                        }
                    }

                    if (streaming) {
                        TokenStream tokenStream = new AiServiceTokenStream((List) messages, (List) toolSpecifications, (Map) toolExecutors, augmentationResult != null ? augmentationResult.contents() : null, NewDefaultAiServices.this.context, memoryId);
                        return returnType == TokenStream.class ? tokenStream : this.adapt(tokenStream, returnType);
                    } else {
                        Response response;
                        if (supportsJsonSchema && jsonSchema.isPresent()) {
                            ChatRequest chatRequest = ChatRequest.builder().messages((List) messages).toolSpecifications((List) toolSpecifications).responseFormat(ResponseFormat.builder().type(ResponseFormatType.JSON).jsonSchema((JsonSchema) jsonSchema.get()).build()).build();
                            ChatResponse chatResponse = NewDefaultAiServices.this.context.chatModel.chat(chatRequest);
                            response = new Response(chatResponse.aiMessage(), chatResponse.tokenUsage(), chatResponse.finishReason());
                        } else {
                            response = toolSpecifications == null ? NewDefaultAiServices.this.context.chatModel.generate((List) messages) : NewDefaultAiServices.this.context.chatModel.generate((List) messages, (List) toolSpecifications);
                        }

                        TokenUsage tokenUsageAccumulator = response.tokenUsage();
                        NewAiServices.verifyModerationIfNeeded(moderationFuture);
                        int executionsLeft = 100;

                        for (List<ToolExecution> toolExecutions = new ArrayList(); executionsLeft-- != 0; tokenUsageAccumulator = TokenUsage.sum(tokenUsageAccumulator, response.tokenUsage())) {
                            AiMessage aiMessage = (AiMessage) response.content();
                            if (NewDefaultAiServices.this.context.hasChatMemory()) {
                                NewDefaultAiServices.this.context.chatMemory(memoryId).add(aiMessage);
                            } else {
                                messages = new ArrayList((Collection) messages);
                                ((List) messages).add(aiMessage);
                            }

                            if (!aiMessage.hasToolExecutionRequests()) {
                                response = Response.from((AiMessage) response.content(), tokenUsageAccumulator, response.finishReason());
                                Object parsedResponse = NewDefaultAiServices.this.serviceOutputParser.parse(response, returnType);
                                if (TypeUtils.typeHasRawClass(returnType, Result.class)) {
                                    return Result.builder().content(parsedResponse).tokenUsage(tokenUsageAccumulator).sources(augmentationResult == null ? null : augmentationResult.contents()).finishReason(response.finishReason()).toolExecutions(toolExecutions).build();
                                }

                                return parsedResponse;
                            }

                            Iterator var21 = aiMessage.toolExecutionRequests().iterator();

                            while (var21.hasNext()) {
                                ToolExecutionRequest toolExecutionRequest = (ToolExecutionRequest) var21.next();
                                ToolExecutor toolExecutor = (ToolExecutor) ((Map) toolExecutors).get(toolExecutionRequest.name());
                                String toolExecutionResult = toolExecutor.execute(toolExecutionRequest, memoryId);
                                toolExecutions.add(ToolExecution.builder().request(toolExecutionRequest).result(toolExecutionResult).build());
                                ToolExecutionResultMessage toolExecutionResultMessage = ToolExecutionResultMessage.from(toolExecutionRequest, toolExecutionResult);
                                if (NewDefaultAiServices.this.context.hasChatMemory()) {
                                    NewDefaultAiServices.this.context.chatMemory(memoryId).add(toolExecutionResultMessage);
                                } else {
                                    ((List) messages).add(toolExecutionResultMessage);
                                }
                            }

                            if (NewDefaultAiServices.this.context.hasChatMemory()) {
                                messages = Collections.singletonList(NewDefaultAiServices.this.context.chatMemory(memoryId).messages());
                            }

                            response = NewDefaultAiServices.this.context.chatModel.generate((List) messages, (List) toolSpecifications);
                        }

                        throw Exceptions.runtime("Something is wrong, exceeded %s sequential tool executions", new Object[]{100});
                    }
                }
            }

            private boolean canAdaptTokenStreamTo(Type returnType) {
                Iterator var21 = NewDefaultAiServices.this.tokenStreamAdapters.iterator();

                TokenStreamAdapter tokenStreamAdapter;
                do {
                    if (!var21.hasNext()) {
                        return false;
                    }

                    tokenStreamAdapter = (TokenStreamAdapter) var21.next();
                } while (!tokenStreamAdapter.canAdaptTokenStreamTo(returnType));

                return true;
            }

            private Object adapt(TokenStream tokenStream, Type returnType) {
                Iterator var3 = NewDefaultAiServices.this.tokenStreamAdapters.iterator();

                TokenStreamAdapter tokenStreamAdapter;
                do {
                    if (!var3.hasNext()) {
                        throw new IllegalStateException("Can't find suitable TokenStreamAdapter");
                    }

                    tokenStreamAdapter = (TokenStreamAdapter) var3.next();
                } while (!tokenStreamAdapter.canAdaptTokenStreamTo(returnType));

                return tokenStreamAdapter.adapt(tokenStream);
            }

            private boolean supportsJsonSchema() {
                return NewDefaultAiServices.this.context.chatModel != null && NewDefaultAiServices.this.context.chatModel.supportedCapabilities().contains(Capability.RESPONSE_FORMAT_JSON_SCHEMA);
            }

            private dev.langchain4j.data.message.UserMessage appendOutputFormatInstructions(Type returnType, dev.langchain4j.data.message.UserMessage userMessage) {
                String outputFormatInstructions = NewDefaultAiServices.this.serviceOutputParser.outputFormatInstructions(returnType);
                String text = userMessage.singleText() + outputFormatInstructions;
                if (Utils.isNotNullOrBlank(userMessage.name())) {
                    userMessage = dev.langchain4j.data.message.UserMessage.from(userMessage.name(), text);
                } else {
                    userMessage = dev.langchain4j.data.message.UserMessage.from(text);
                }

                return userMessage;
            }

            private Future<Moderation> triggerModerationIfNeeded(Method method, List<ChatMessage> messages) {
                return method.isAnnotationPresent(Moderate.class) ? this.executor.submit(() -> {
                    List<ChatMessage> messagesToModerate = NewAiServices.removeToolMessages(messages);
                    return (Moderation) NewDefaultAiServices.this.context.moderationModel.moderate(messagesToModerate).content();
                }) : null;
            }
        });
        return (T) o;
    }

    private Optional<SystemMessage> prepareSystemMessage(Object memoryId, Method method, Object[] args) {
        return this.findSystemMessageTemplate(memoryId, method).map((systemMessageTemplate) -> {
            return PromptTemplate.from(systemMessageTemplate).apply(findTemplateVariables(systemMessageTemplate, method, args)).toSystemMessage();
        });
    }

    private Optional<String> findSystemMessageTemplate(Object memoryId, Method method) {
        dev.langchain4j.service.SystemMessage annotation = (dev.langchain4j.service.SystemMessage)method.getAnnotation(dev.langchain4j.service.SystemMessage.class);
        return annotation != null ? Optional.of(getTemplate(method, "System", annotation.fromResource(), annotation.value(), annotation.delimiter())) : (Optional)this.context.systemMessageProvider.apply(memoryId);
    }

    private static Map<String, Object> findTemplateVariables(String template, Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();
        Map<String, Object> variables = new HashMap();

        for(int i = 0; i < parameters.length; ++i) {
            String variableName = getVariableName(parameters[i]);
            Object variableValue = args[i];
            variables.put(variableName, variableValue);
        }

        if (template.contains("{{it}}") && !variables.containsKey("it")) {
            String itValue = getValueOfVariableIt(parameters, args);
            variables.put("it", itValue);
        }

        return variables;
    }

    private static String getVariableName(Parameter parameter) {
        V annotation = (V)parameter.getAnnotation(V.class);
        return annotation != null ? annotation.value() : parameter.getName();
    }

    private static String getValueOfVariableIt(Parameter[] parameters, Object[] args) {
        if (parameters.length == 1) {
            Parameter parameter = parameters[0];
            if (!parameter.isAnnotationPresent(MemoryId.class) && !parameter.isAnnotationPresent(UserMessage.class) && !parameter.isAnnotationPresent(UserName.class) && (!parameter.isAnnotationPresent(V.class) || isAnnotatedWithIt(parameter))) {
                return toString(args[0]);
            }
        }

        for(int i = 0; i < parameters.length; ++i) {
            if (isAnnotatedWithIt(parameters[i])) {
                return toString(args[i]);
            }
        }

        throw IllegalConfigurationException.illegalConfiguration("Error: cannot find the value of the prompt template variable \"{{it}}\".");
    }

    private static boolean isAnnotatedWithIt(Parameter parameter) {
        V annotation = (V)parameter.getAnnotation(V.class);
        return annotation != null && "it".equals(annotation.value());
    }

    private static dev.langchain4j.data.message.UserMessage prepareUserMessage(Method method, Object[] args) {
        String template = getUserMessageTemplate(method, args);
        Map<String, Object> variables = findTemplateVariables(template, method, args);
        Prompt prompt = PromptTemplate.from(template).apply(variables);
        Optional<String> maybeUserName = findUserName(method.getParameters(), args);
        Optional var10000 = maybeUserName.map((userName) -> {
            return dev.langchain4j.data.message.UserMessage.from(userName, prompt.text());
        });
        Objects.requireNonNull(prompt);
        return (dev.langchain4j.data.message.UserMessage)var10000.orElseGet(prompt::toUserMessage);
    }

    private static String getUserMessageTemplate(Method method, Object[] args) {
        Optional<String> templateFromMethodAnnotation = findUserMessageTemplateFromMethodAnnotation(method);
        Optional<String> templateFromParameterAnnotation = findUserMessageTemplateFromAnnotatedParameter(method.getParameters(), args);
        if (templateFromMethodAnnotation.isPresent() && templateFromParameterAnnotation.isPresent()) {
            throw IllegalConfigurationException.illegalConfiguration("Error: The method '%s' has multiple @UserMessage annotations. Please use only one.", new Object[]{method.getName()});
        } else if (templateFromMethodAnnotation.isPresent()) {
            return (String)templateFromMethodAnnotation.get();
        } else if (templateFromParameterAnnotation.isPresent()) {
            return (String)templateFromParameterAnnotation.get();
        } else {
            Optional<String> templateFromTheOnlyArgument = findUserMessageTemplateFromTheOnlyArgument(method.getParameters(), args);
            if (templateFromTheOnlyArgument.isPresent()) {
                return (String)templateFromTheOnlyArgument.get();
            } else {
                throw IllegalConfigurationException.illegalConfiguration("Error: The method '%s' does not have a user message defined.", new Object[]{method.getName()});
            }
        }
    }

    private static Optional<String> findUserMessageTemplateFromMethodAnnotation(Method method) {
        return Optional.ofNullable((UserMessage)method.getAnnotation(UserMessage.class)).map((a) -> {
            return getTemplate(method, "User", a.fromResource(), a.value(), a.delimiter());
        });
    }

    private static Optional<String> findUserMessageTemplateFromAnnotatedParameter(Parameter[] parameters, Object[] args) {
        for(int i = 0; i < parameters.length; ++i) {
            if (parameters[i].isAnnotationPresent(UserMessage.class)) {
                return Optional.of(toString(args[i]));
            }
        }

        return Optional.empty();
    }

    private static Optional<String> findUserMessageTemplateFromTheOnlyArgument(Parameter[] parameters, Object[] args) {
        return parameters != null && parameters.length == 1 && parameters[0].getAnnotations().length == 0 ? Optional.of(toString(args[0])) : Optional.empty();
    }

    private static Optional<String> findUserName(Parameter[] parameters, Object[] args) {
        for(int i = 0; i < parameters.length; ++i) {
            if (parameters[i].isAnnotationPresent(UserName.class)) {
                return Optional.of(args[i].toString());
            }
        }

        return Optional.empty();
    }

    private static String getTemplate(Method method, String type, String resource, String[] value, String delimiter) {
        String messageTemplate;
        if (!resource.trim().isEmpty()) {
            messageTemplate = getResourceText(method.getDeclaringClass(), resource);
            if (messageTemplate == null) {
                throw IllegalConfigurationException.illegalConfiguration("@%sMessage's resource '%s' not found", new Object[]{type, resource});
            }
        } else {
            messageTemplate = String.join(delimiter, value);
        }

        if (messageTemplate.trim().isEmpty()) {
            throw IllegalConfigurationException.illegalConfiguration("@%sMessage's template cannot be empty", new Object[]{type});
        } else {
            return messageTemplate;
        }
    }

    private static String getResourceText(Class<?> clazz, String resource) {
        InputStream inputStream = clazz.getResourceAsStream(resource);
        if (inputStream == null) {
            inputStream = clazz.getResourceAsStream("/" + resource);
        }

        return getText(inputStream);
    }

    private static String getText(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        } else {
            Scanner scanner = new Scanner(inputStream);

            String var3;
            try {
                Scanner s = scanner.useDelimiter("\\A");

                try {
                    var3 = s.hasNext() ? s.next() : "";
                } catch (Throwable var7) {
                    if (s != null) {
                        try {
                            s.close();
                        } catch (Throwable var6) {
                            var7.addSuppressed(var6);
                        }
                    }

                    throw var7;
                }

                if (s != null) {
                    s.close();
                }
            } catch (Throwable var8) {
                try {
                    scanner.close();
                } catch (Throwable var5) {
                    var8.addSuppressed(var5);
                }

                throw var8;
            }

            scanner.close();
            return var3;
        }
    }

    private static Optional<Object> findMemoryId(Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();

        for(int i = 0; i < parameters.length; ++i) {
            if (parameters[i].isAnnotationPresent(MemoryId.class)) {
                Object memoryId = args[i];
                if (memoryId == null) {
                    throw Exceptions.illegalArgument("The value of parameter '%s' annotated with @MemoryId in method '%s' must not be null", new Object[]{parameters[i].getName(), method.getName()});
                }

                return Optional.of(memoryId);
            }
        }

        return Optional.empty();
    }

    private static String toString(Object arg) {
        if (arg.getClass().isArray()) {
            return arrayToString(arg);
        } else {
            return arg.getClass().isAnnotationPresent(StructuredPrompt.class) ? StructuredPromptProcessor.toPrompt(arg).text() : arg.toString();
        }
    }

    private static String arrayToString(Object arg) {
        StringBuilder sb = new StringBuilder("[");
        int length = Array.getLength(arg);

        for(int i = 0; i < length; ++i) {
            sb.append(toString(Array.get(arg, i)));
            if (i < length - 1) {
                sb.append(", ");
            }
        }

        sb.append("]");
        return sb.toString();
    }
}
