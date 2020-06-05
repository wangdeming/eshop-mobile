package cn.ibdsr.web.core.util;

import cn.ibdsr.core.util.SpringContextHolder;
import cn.ibdsr.core.util.ToolUtil;
import cn.ibdsr.fastdfs.base.service.FdfsClientService;
import cn.ibdsr.fastdfs.config.FastdfsProperties;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 图片路径拼接工具类
 *
 * @author XuZhipeng
 * @Date 2019-02-21 11:26:18
 */
public class ImageUtil {

    /**
     * 图片访问的前缀
     */
    public static String PREFIX_IMAGE_URL = SpringContextHolder.getBean(FastdfsProperties.class).getVisit();

    private static FdfsClientService fdfsClientService = SpringContextHolder.getBean(FdfsClientService.class);

    /**
     * 根据图片URL上传图片
     *
     * @param url
     * @return
     * @throws IOException
     * @throws MimeTypeException
     */
    public static String upload(String url) throws IOException, MimeTypeException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet method = new HttpGet(url);
        HttpResponse result = httpClient.execute(method);
        if (result.getStatusLine().getStatusCode() != 200) {
            return null;
        } else {
            MimeTypes mimeTypes = MimeTypes.getDefaultMimeTypes();
            MimeType mimeType = mimeTypes.forName(result.getHeaders("Content-Type")[0].getValue());
            return fdfsClientService.uploadFile(IOUtils.toByteArray(result.getEntity().getContent()), StringUtils.substringAfterLast(mimeType.getExtension(), "."));
        }
    }

    /**
     * @param targetImageURL 目标的图片的URL
     * @return
     * @throws
     * @Description 裁剪 图片的URL
     * 比如将http://172.16.1.200:10080/group1/M00/00/00/rBAByFmDy-WAT7FXAALo7BXRELY084.jpg
     * 截取为 group1/M00/00/00/rBAByFmDy-WAT7FXAALo7BXRELY084.jpg
     * @Date 2017/8/17 15:33
     */
    public static String cutImageURL(String targetImageURL) {
        if (StringUtils.isEmpty(targetImageURL)) {
            return targetImageURL;
        }
        int imageIndex = targetImageURL.indexOf(PREFIX_IMAGE_URL);
        if (imageIndex < 0) {
            return targetImageURL;
        }
        return targetImageURL.substring(imageIndex + PREFIX_IMAGE_URL.length());
    }

    /**
     * @param targetImageURL 目标的图片的URL
     * @return
     * @throws
     * @Description 补全 图片的URL
     * 比如将/group1/M00/00/00/rBAByFmDy-WAT7FXAALo7BXRELY084.jpg
     * 补全为 http://172.16.1.200:10080/group1/M00/00/00/rBAByFmDy-WAT7FXAALo7BXRELY084.jpg
     * @Date 2017/8/17 15:33
     */
    public static String setImageURL(String targetImageURL) {
        if (StringUtils.isEmpty(targetImageURL) || "null".equals(targetImageURL)) {
            return "";
        }
        int imageIndex = targetImageURL.indexOf(PREFIX_IMAGE_URL);
        if (imageIndex > -1) {
            return targetImageURL;
        }
        return PREFIX_IMAGE_URL + targetImageURL;
    }

    /**
     * 拼接多个以","隔开的图片字符串
     *
     * @param imgstr 以","隔开的图片字符串
     * @return
     */
    public static String setImageStrURL(String imgstr) {
        if (ToolUtil.isEmpty(imgstr)) {
            return "";
        }
        List<String> imgList = Arrays.asList(imgstr.split(","));
        for (int i = 0, length = imgList.size(); i < length; i++) {
            imgList.set(i, ImageUtil.setImageURL(imgList.get(i)));
        }
        String jsonstr = JSONArray.toJSON(imgList).toString();
        return jsonstr;
    }

    /**
     * 拼接多个以","隔开的图片字符串
     *
     * @param imgstr 以","隔开的图片字符串
     * @return
     */
    public static List<String> setImageStrURL2List(String imgstr) {
        if (ToolUtil.isEmpty(imgstr)) {
            return null;
        }
        List<String> imgList = Arrays.asList(imgstr.split(","));
        for (int i = 0, length = imgList.size(); i < length; i++) {
            imgList.set(i, ImageUtil.setImageURL(imgList.get(i)));
        }
        return imgList;
    }

    /**
     * 截去图片集合中的图片路径前缀，并转为以","隔开的字符串
     *
     * @param imgList 图片集合
     * @return
     */
    public static String cutImageListURL2Str(List<String> imgList) {
        if (imgList == null) {
            return null;
        }
        for (int i = 0, length = imgList.size(); i < length; i++) {
            imgList.set(i, ImageUtil.cutImageURL(imgList.get(i)));
        }
        return StringUtils.join(imgList, ",");
    }

}
