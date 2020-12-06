package com.kuan.jcc;

import com.google.protobuf.ByteString;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author kuan
 * Created on 2019/7/19.
 * @description
 */
public class JCCDemo extends ChaincodeBase {
    private static Log logger = LogFactory.getLog(JCCDemo.class);

    public static void main(String[] args) {
        logger.info("JCCDemo start");
        new JCCDemo().start(args);
    }

    @Override
    public Response init(ChaincodeStub stub) {
        logger.info("start init");
        try {
            String func = stub.getFunction();

            if (!func.equals("init")) {
                return newErrorResponse("fun name must be init");
            }

            stub.putStringState("init","this is init");
            return newSuccessResponse("init success");
        } catch (Exception e) {
            return newErrorResponse("error :" + e.getMessage());
        }
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        try {
            logger.info("Invoke java simple chaincode");
            String func = stub.getFunction();
            List<String> params = stub.getParameters();

            switch (func) {
                case "set":
                    return set(stub,params);
                case "delete":
                    return delete(stub,params);
                case "update":
                    return update(stub,params);
                case "get":
                    return get(stub,params);
                case "invokeChainCode":
                    return invokeChainCode(stub,params);
                case "invokeChainCodeWithChannel":
                    return invokeChainCodeWithChannel(stub,params);
                default:
                    return newErrorResponse("can't invoke \""+func+"\"");
            }
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    /**
     * @Author kuan
     * @Date 14:17 2019/7/19
     * @Description add data
     * @Param [stub, params]
     * @return Response
     **/
    public Response set(ChaincodeStub stub,List<String> params){
        try {
            if (params.size() != 2){
                return newErrorResponse("invalide params nums");
            }

            String key = params.get(0);
            String value = params.get(1);

            stub.putState(key,value.getBytes());
            Map<String, byte[]> transientMap = stub.getTransient();
            if (null != transientMap) {
                if (transientMap.containsKey("event") && transientMap.get("event") != null) {
                    stub.setEvent("event", transientMap.get("event"));
                }
                if (transientMap.containsKey("result") && transientMap.get("result") != null) {
                    return newSuccessResponse(transientMap.get("result"));
                }
            }

            return newSuccessResponse("add data success");

        } catch (Throwable e){
            return newErrorResponse(e);
        }
    }

    /**
     * @Author kuan
     * @Date 14:17 2019/7/19
     * @Description delete data success
     * @Param [stub, params]
     * @return Response
     **/
    public Response delete(ChaincodeStub stub,List<String> params){
        try {
            if (params.size() != 1){
                return newErrorResponse("invalide params nums");
            }
            String key = params.get(0);
            stub.delState(key);
            return newSuccessResponse("delete data success");

        } catch (Throwable e){
            return newErrorResponse(e);
        }
    }

    /**
     * @Author kuan
     * @Date 14:18 2019/7/19
     * @Description update data
     * @Param [stub, params]
     * @return Response
     **/
    public Response update(ChaincodeStub stub,List<String> params){
        try {
            if (params.size() != 2){
                return newErrorResponse("invalide params nums");
            }

            String key = params.get(0);
            String value = params.get(1);
            stub.putState(key,value.getBytes());

            Map<String, byte[]> transientMap = stub.getTransient();
            if (null != transientMap) {
                if (transientMap.containsKey("event") && transientMap.get("event") != null) {
                    stub.setEvent("event", transientMap.get("event"));
                }
                if (transientMap.containsKey("result") && transientMap.get("result") != null) {
                    return newSuccessResponse(transientMap.get("result"));
                }
            }

            return newSuccessResponse("update data success");

        } catch (Throwable e){
            return newErrorResponse(e);
        }
    }

    /**
     * @Author kuan
     * @Date 14:18 2019/7/19
     * @Description get data
     * @Param [stub, params]
     * @return Response
     **/
    public Response get(ChaincodeStub stub,List<String> params){
        try {
            if (params.size() != 1){
                return newErrorResponse("invalide params nums");
            }

            String key = params.get(0);
            //byte[] stateBytes
            String val = stub.getStringState(key);
            if (val == null) {
                return newErrorResponse(String.format("key %s is not exit", key));
            }

            return newSuccessResponse(val, ByteString.copyFrom(val, UTF_8).toByteArray());

        } catch (Throwable e){
            return newErrorResponse(e);
        }
    }

    /**
     * @Author kuan
     * @Date 14:18 2019/7/19
     * @Description invoke other channel data
     * @Param [stub, params]
     * @return Response
     **/
    public Response invokeChainCode(ChaincodeStub stub,List<String> params){
        try {
            if (params.size() <= 2){
                return newErrorResponse("invalide params nums");

            }

            String chainCodeName = params.get(0);
            List<String> args = params.subList(1,params.size());
            return stub.invokeChaincodeWithStringArgs(chainCodeName,args);

        } catch (Throwable e){
            return newErrorResponse(e);
        }
    }

    /**
     * @Author kuan
     * @Date 14:18 2019/7/19
     * @Description invoke other channel data
     * @Param [stub, params]
     * @return Response
     **/
    public Response invokeChainCodeWithChannel(ChaincodeStub stub,List<String> params){
        try {
            if (params.size() <= 3){
                return newErrorResponse("invalide params nums");
            }

            String chainCodeName = params.get(0);
            String channelName = params.get(1);
            List<String> args = params.subList(2,params.size());
            return stub.invokeChaincodeWithStringArgs(chainCodeName,args,channelName);

        } catch (Throwable e){
            return newErrorResponse(e);
        }
    }
}
