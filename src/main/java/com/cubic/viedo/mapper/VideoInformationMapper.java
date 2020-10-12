package com.cubic.viedo.mapper;

import com.cubic.viedo.module.VideoDo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 操作
 *
 * @author QIANGLU on 2020/7/27
 */
@Mapper
public interface VideoInformationMapper {

    /**
     * insert base data
     *
     * @param videoDo
     * @return
     */
    @Insert("INSERT INTO video_information(service_id,org_file_name,file_name,file_url,data,type,create_date,modify_date) VALUES(#{serviceId},#{orgFileName},#{fileName},#{fileUrl},#{data},#{type},#{createDate},#{modifyDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(VideoDo videoDo);

    /**
     * 根据serviceId 更新文件url
     *
     * @param url
     * @param serviceId
     */
    @Update("update video_information set file_url =#{url} where service_id =#{serviceId}")
    void updateVideoUrlByServiceId(@Param("url") String url, @Param("serviceId") String serviceId);

    /**
     * 根据serviceId 进行数据查询
     *
     * @param serviceId
     * @return
     */
    @Select("SELECT count(*) FROM video_information where service_id = #{serviceId}")
    Long selectCountByServiceId(@Param("serviceId") String serviceId);

    /**
     * 根据serviceId 进行数据查询
     *
     * @param serviceId
     * @return
     */
    @Select("SELECT file_url FROM video_information where service_id = #{serviceId}")
    String selectByServiceId(@Param("serviceId") String serviceId);

    @Select("<script> " +
            "SELECT service_id serviceId,org_file_name orgFileName,file_name fileName,file_url fileUrl,data data,create_date createDate,modify_date modifyDate,type type FROM video_information " +
            "<when test='serviceId !=null'> " +
            "  where service_id = #{serviceId} " +
            "</when>" +
            "order by id  desc limit #{total} " +
            "</script>")
    List<VideoDo> selectVideos(@Param("total") Integer total, @Param("serviceId") String serviceId);


//    @Delete("DELETE from inform_chart_relation where appId = #{appId}")
//    public int deleteByAppId(@Param("appId")String appId);
//
//
//    /**
//     * 查询映射关系
//     *
//     * @return
//     */
//    @Select("SELECT * FROM inform_chart_relation ")
//    List<InformChartRelation> selectRelations();
//
//    /**
//     * 查询映射关系
//     *
//     * @return
//     */
//    @Select("SELECT * FROM inform_chart_relation where appId = #{appId}")
//    InformChartRelation selectRelationByAppId(@Param("appId") String appId);
//

//
//
//    @Insert({
//            "<script>",
//            "INSERT INTO inform_chart_relation(appId,chartId,createDate) VALUES ",
//            "<foreach collection='relations' item='item' index='index' separator=','>",
//            "(#{item.appId},#{item.chartId},#{item.createDate})",
//            "</foreach>",
//            "</script>"
//    })
//    int insertRelations(@Param(value = "relations") List<InformChartRelation> relations);

}
