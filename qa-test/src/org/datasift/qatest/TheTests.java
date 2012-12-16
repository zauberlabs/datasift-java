package org.datasift.qatest;
import org.datasift.qatest.interfaces.*;
import java.util.Map;
import java.util.HashMap;

//region compile/validate
    class TestValidate implements Test3{
        public Map<String, Object> Test(String csdl, String userName, String userKey){
            return TestHelper.validateOrCompile(CompileMode.ValidateOnly, csdl, userName, userKey);
        }
    }
    class TestCompile implements Test3 {
        public Map<String, Object> Test(String csdl, String userName, String userKey) {
            return TestHelper.validateOrCompile(CompileMode.Compile, csdl, userName, userKey);
        }
    }
    //endregion

    //region dpu
    class TestDpuFromHash implements Test3 {
        public Map<String, Object> Test(String csdlhash, String userName, String userKey) {
        	org.datasift.User user = new org.datasift.User(userName, userKey);
        	org.datasift.Definition def = new org.datasift.Definition(user, "",csdlhash);  
            return JsonHelpers.DpuBreakdownAsDictionary(def.getDPUBreakdown());
        }
    }
    class TestDpuFromCsdl implements Test3 {
        public Map<String, Object> Test(String csdl, String userName, String userKey) {
            org.datasift.Definition def = TestHelper.DefinitionFromCsdl(csdl, userName, userKey);
            return JsonHelpers.DpuBreakdownAsDictionary(def.getDPUBreakdown());
        }
    }
    //endregion

    //region usage
    class TestUsage implements Test3 {
        public Map<String, Object> Test(String period, String userName, String userKey) {
        	org.datasift.User user = new org.datasift.User(userName, userKey);
        	org.datasift.Usage usage = user.getUsage(period);
            Map<String, Object> Result = new HashMap<String, Object>();
            Result.put("start", usage.getStartDate());
            Result.put("end", usage.getEndDate());
            Map<String, Object> HashsInfo = new HashMap<String, Object>();
            foreach (var hash in usage.getStreamHashes()) {
            	Map<String, Object> HashInfo = new HashMap<String, Object>();
            	Map<String, Object> TypesInfo = new HashMap<String, Object>();
                foreach (var type in usage.getLicenseTypes(hash)) {
                    TypesInfo.put(type, usage.getLicenseUsage(hash, type));
                }
                HashInfo.put("licenses", TypesInfo);
                HashInfo.put("seconds", usage.getSeconds(hash));
                HashsInfo.put(hash, HashInfo);
            }
            Result.put("streams", HashsInfo);
            return Result;
        }
    }
    //endregion

    //region stream
    class TestStreamCsdl extends TestStreamHelper implements Test4 
    {
        public Map<String, Object> Test(String connectionType, String csdl, String userName, String userKey) 
        {
            org.datasift.Definition def = TestHelper.DefinitionFromCsdl(csdl, userName, userKey);
            return TestStream(connectionType, def);
        }
    }

    class TestStreamX extends TestStreamHelper implements Test5
    {
        public Map<String, Object> Test(String connectionType, String csdl, String hash, String userName, String userKey)
        {
            org.datasift.Definition def = TestHelper.DefinitionFromX(csdl, hash, userName, userKey);
            return TestStream(connectionType, def);
        }
    }

    class TestStreamHelper
    {
        public Map<String, Object> TestStream(String connectionType, org.datasift.Definition def)
        {
            var consumer = def.getConsumer(connectionType, new EventHandlers());
            consumer.consume();
            //can get here?

            Map<String, Object> Result = new HashMap<String, Object>();
            return Result;
        }
    }

    //endregion

    class TestHelper {
        //region compile/validate
        //public delegate void compileDeligate(org.datasift.Definition definition);
        public static Map<String, Object> validateOrCompile(
            CompileMode mode, String csdl, String userName, String userKey) 
        {
            org.datasift.Definition def = DefinitionFromCsdl(csdl, userName, userKey);
            switch (mode)
            {
                case Compile:
                    def.compile();
                    break;
                case ValidateOnly:
                    def.validate();
                    break;
            }
            Map<String,Object> Result = JsonHelpers.definitionAsDictionary(def,mode);
            return Result;
        }
        //endregion

        //region definition
        public static org.datasift.Definition DefinitionFromCsdl(String csdl, String userName, String userKey) {
        	org.datasift.Definition Result = new org.datasift.User(userName, userKey).createDefinition(csdl);
            //Assert.That( Result, Is.TypeOf<org.datasift.Definition>());
            assert(Result!=null);
            return Result;
        }

        public static org.datasift.Definition DefinitionFromX(String csdl, String hash, String userName, String userKey)
        {
        	org.datasift.Definition Result = new org.datasift.Definition(new org.datasift.User(userName, userKey), csdl, hash);
            //Assert.That(Result, Is.TypeOf<org.datasift.Definition>());
            assert(Result != null);
            return Result;
        }
        //endregion
    }

    enum CompileMode { Compile, ValidateOnly };
    class JsonHelpers {
        //region definition
        public static Map<String, Object> definitionAsDictionary(
            org.datasift.Definition def, CompileMode type) {
        	Map<String, Object> Result = new HashMap<String, Object>();
            {
                Result.put("createdAt", def.getCreatedAt());
                Result.put("user", userAsDictionary(def.getUser()));
                Result.put("csdl", def.get());
                Result.put("dpuTotal", Double.toString(def.getTotalDPU()));
                if (type==CompileMode.Compile) { 
                    Result.put("hash", def.getHash());
                }
            }
            return Result;
        }
        //endregion

        //region dpu
        public static Map<String, Object> DpuBreakdownAsDictionary(org.datasift.DPU dpu) {
        	Map<String, Object> Result = new HashMap<String, Object>();
            {
            	Map<String, Object> breakDown = new HashMap<String, Object>();
                {
                    breakDown.put("dpu", dpu.getTotal());
                    breakDown.put("detail", DpuItemDictionaryAsDictionary(dpu.getDPU()));
                }
                Result.put("dpuBreakdown", breakDown);
            }
            return Result;
        }

        //:warning: recursion without tail-call optimisation
        public static Map<String, Object> DpuItemDictionaryAsDictionary(Map<String, org.datasift.DPUItem> dpuItems) {
        	Map<String, Object> Result = new HashMap<String, Object>();
            {
            	java.util.Iterator<String> it = dpuItems.keySet().iterator();
            	while (it.hasNext())
            	{
            		String key = it.next();
            		org.datasift.DPUItem value = dpuItems.get(key);
            		Result.put(key, DpuItemAsDictionary(value));
            	}
            }
            return Result;
        }

        //:warning: recursion without tail-call optimisation
        public static Map<String, Object> DpuItemAsDictionary(org.datasift.DPUItem dpuItem) {
        	Map<String, Object> Result = new HashMap<String, Object>();
            {
                Result.put("count", dpuItem.getCount());
                Result.put("dpu", dpuItem.getDPU());
                Result.put("targets", DpuItemDictionaryAsDictionary(dpuItem.getTargets()));
            }
            return Result;
        }
        //endregion

        //region user
        public static Map<String, Object> userAsDictionary(org.datasift.User user) {
        	Map<String, Object> Result = new HashMap<String, Object>();
            {
                Result.put("name", user.getUsername());
                Result.put("apiKey", user.getAPIKey());
                Result.put("rateLimit", user.getRateLimit());
                Result.put("rateLimitRemaining", user.getRateLimitRemaining());
                //...etc...
            }
            return Result;
        }
        //endregion

        //region interaction
        public static Map<String, Object> interactionAsDictionary(org.datasift.JSONdn interaction)
        {
            return a(interaction.getJVal(""));
        }

        public static Map<String, Object> a(Newtonsoft.Json.Linq.JToken token)
        {
            return new HashMap<String, Object>();
        }

        public static Map<String, Object> _interactionAsDictionary(org.datasift.JSONdn interaction, String root) {
        	Map<String, Object> Result = new HashMap<String, Object>();
            {
                var keys = interaction.getKeys();
                Result["keys"] = keys;
                foreach (var key in keys)
                {
                    var t = interaction.getJVal("");
                    
                    
                }
                
            }
            return Result;
        }
        //endregion

        //region consumer
        public static Map<String, Object> consumerAsDictionary(org.datasift.StreamConsumer consumer) {
        	Map<String, Object> Result = new HashMap<String, Object>();
            {
                
            }
            return Result;
        }
        //endregion

        public static  Map<String, Object> ObjectAsDictionary(String s,Object o) {
        	Map<String, Object> Result = new HashMap<String, Object>();
            {
                Result.put(s, o);
            }
            return Result;
        }
    }

    class EventHandlers implements org.datasift.IStreamConsumerEvents {
        private Logger _theLogger = new LogFactory().theLogger();
        private JsonHelpers JsonHelpers = new JsonHelpers();

        //region IEventHandler Members

        public void onConnect(org.datasift.StreamConsumer consumer) {
            _theLogger.logEvent("connected",
                new Object[] {
                    JsonHelpers.consumerAsDictionary(consumer)
                });  
        }

        public void onDeleted(org.datasift.StreamConsumer consumer, org.datasift.Interaction interaction, String hash) {
            _theLogger.logEvent( "delete",
                new Object[] {
                    JsonHelpers.ObjectAsDictionary("hash",hash),
                    JsonHelpers.interactionAsDictionary(interaction),
                    JsonHelpers.consumerAsDictionary(consumer)
                });  
        }

        public void onDisconnect(org.datasift.StreamConsumer consumer) {
            _theLogger.logEvent("dissconneted",
                new Object[] {
                    JsonHelpers.consumerAsDictionary(consumer)
                });  
        }

        public void onError(org.datasift.StreamConsumer consumer, String message) {
            _theLogger.logEvent("error",
                new Object[] {
                    JsonHelpers.ObjectAsDictionary("message",message),
                    JsonHelpers.consumerAsDictionary(consumer)
                });  
        }

        public void onInteraction(org.datasift.StreamConsumer consumer, org.datasift.Interaction interaction, String hash) {
            _theLogger.logEvent("interaction",
                new Object[] {
                    JsonHelpers.ObjectAsDictionary("hash",hash),
                    JsonHelpers.interactionAsDictionary(interaction),
                    JsonHelpers.consumerAsDictionary(consumer)
                });  
        }

        public void onWarning(org.datasift.StreamConsumer consumer, String message) {
            _theLogger.logEvent("warning",
               new Object[] {
                    JsonHelpers.ObjectAsDictionary("message",message),
                    JsonHelpers.consumerAsDictionary(consumer)
                });  
        }

        //endregion
    }

