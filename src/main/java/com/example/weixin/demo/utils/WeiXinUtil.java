package com.example.weixin.demo.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import javax.net.ssl.HttpsURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.weixin.demo.model.AccessToken;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class WeiXinUtil {

	private static Logger logger = LoggerFactory.getLogger(WeiXinUtil.class);
	/**
	 * 微信接入验证
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
	private static final String token = "mytoken123456";//自己在微信测试平台设置的token
	private static final String appid = "wx382cc74f49d6ffa7";//微信公众平台的appid
	private static final String appsecret = "5120977be2aedcc669021b8733d4b955";//微信公众平台的appsecret
	//获取access_token 接口
	public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?"
	+ "grant_type=client_credential&appid=APPID&secret=APPSECRET";
	//删除菜单接口
	public static final String DELETE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";
	//添加菜单接口
	public static final String ADD_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	//解析后的域名
	public static final String NATURL= "http://vqyhbh.natappfree.cc";
	//新增临时素材的接口
	public static final String ADD_FILE_TEMPORARY= "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
	//发送模板消息的接口
	public static final String SEND_TEMPLATE_MSG= "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
	//生成二维码的接口
	public static final String CREATE_CODE= "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=ACCESS_TOKEN";
	//展示二维码的接口
	public static final String SHOW_CODE= "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET";
	/**
	 * 
	 * @param signature 微信加密签名
	 * @param timestamp 时间戳
	 * @param nonce 随机数
	 * @return
	 */
	public static boolean checkSignature(String signature,String timestamp,String nonce){
		String[] str = new String[]{token,timestamp,nonce};
		//排序
		Arrays.sort(str);
		//拼接字符串
		StringBuffer buffer = new StringBuffer();
		for(int i =0 ;i<str.length;i++){
			buffer.append(str[i]);
		}
		//进行sha1加密
		String temp = SHA1.encode(buffer.toString());
		//与微信提供的signature进行匹对
		return signature.equals(temp);
	}
	
	
    /** 
     * 获取access_token 
     * access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
     * @param appid 凭证 
     * @param appsecret 密钥 
     * @return 
     */  
    public static String  getAccessToken() {  
        AccessToken accessToken = null;  
        String requestUrl = ACCESS_TOKEN_URL.replace("APPID", appid).replace("APPSECRET", appsecret);  
        JSONObject jsonObject = HttpUtil.doGetstr(requestUrl);
        if (null != jsonObject) {  
            try {  
                accessToken = new AccessToken();  
                accessToken.setAccess_token(jsonObject.getString("access_token"));  
                accessToken.setExpires_in(jsonObject.getInt("expires_in"));  
            } catch (JSONException e) {  
                accessToken = null;  
                // 获取token失败  
                logger.error("获取token失败 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));  
            }  
        }  
        return accessToken.getAccess_token();  
    }  
    
    /**
	 * 文件上传的方法
	 * @param filePath
	 * @param
	 * @return
	 */
	public static String upload(String filePath,String fileType){
		String result=null;
		String mediaId=null;
		File file=new File(filePath);
		try {
			if(!file.exists()||!file.isFile()){
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String  token=WeiXinUtil.getAccessToken();
			String urlString=ADD_FILE_TEMPORARY.replace("ACCESS_TOKEN", token).replace("TYPE", fileType);
			URL url=new URL(urlString);
			HttpsURLConnection conn=(HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("POST");//以POST方式提交表单
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);//POST方式不能使用缓存
			//设置请求头信息
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			//设置边界
			String BOUNDARY="----------"+System.currentTimeMillis();
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
			//请求正文信息
			//第一部分
			StringBuilder sb=new StringBuilder();
			sb.append("--");//必须多两条道
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data;name=\"media\"; filename=\"" + file.getName()+"\"\r\n");
			sb.append("Content-Type:application/octet-stream\r\n\r\n");
			System.out.println("sb:"+sb);

			//获得输出流
			OutputStream out=new DataOutputStream(conn.getOutputStream());
			//输出表头
			out.write(sb.toString().getBytes("UTF-8"));
			//文件正文部分
			//把文件以流的方式 推送道URL中
			DataInputStream din=new DataInputStream(new FileInputStream(file));
			int bytes=0;
			byte[] buffer=new byte[1024];
			while((bytes=din.read(buffer))!=-1){
				out.write(buffer,0,bytes);
			}
			din.close();
			//结尾部分
			byte[] foot=("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");//定义数据最后分割线
			out.write(foot);
			out.flush();
			out.close();
			if(HttpsURLConnection.HTTP_OK==conn.getResponseCode()){

				StringBuffer strbuffer=null;
				BufferedReader reader=null;
				try {
					strbuffer=new StringBuffer();
					reader=new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String lineString=null;
					while((lineString=reader.readLine())!=null){
						strbuffer.append(lineString);

					}
					if(result==null){
						result=strbuffer.toString();
						logger.info("result:"+result);
						/**
						 * 新增临时素材和永久素材的返回值不同
						 * 临时：{"type":"TYPE","media_id":"MEDIA_ID","created_at":123456789}
						 * 永久：
						 */
						JSONObject jsonObj = JSONObject.fromObject(result);
						System.out.println(jsonObj);
						String typeName = "media_id";
						if(!"image".equals(fileType)){
							typeName = fileType + "_media_id";
						}
						mediaId = jsonObj.getString(typeName);
					}
				} catch (IOException e) {
					logger.error("发送POST请求出现异常！",e);
					e.printStackTrace();
				}finally{
					if(reader!=null){
						reader.close();
					}
				}

			}
		}  catch (IOException e) {
			e.printStackTrace();
		}
		return mediaId;
	}

	/**
	 * 生成带参数的二维码
	 * @return
	 */
	public static String cerateCode() {
		String url  = WeiXinUtil.CREATE_CODE.replace("ACCESS_TOKEN", WeiXinUtil.getAccessToken());
		String code = JSONObject.fromObject(MsgUtil.initStrCode()).toString();
		JSONObject result = HttpUtil.doPoststr(url,code);
		if("ok".equals(result.getString("ticket"))){
			System.out.println("result"+result);
        }
		return result.getString("ticket");
	}

	
}
