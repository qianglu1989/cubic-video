package com.cubic.viedo.mapper;

import com.cubic.viedo.monitor.VideoChannelDo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 操作
 *
 * @author QIANGLU on 2020/7/27
 */
@Mapper
public interface VideoChannelsMapper {

    /**
     * insert base data
     *
     * @param videoChannelDo
     * @return
     */
    @Insert("INSERT INTO video_channels(remote_address,server_address,file_name,state,data,create_date,modify_date,channel_id) VALUES(#{remoteAddress},#{serverAddress},#{fileName},#{state},#{data},#{createDate},#{modifyDate},#{channelId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(VideoChannelDo videoChannelDo);

    /**
     * @param channelId
     * @param state
     */
    @Update("update video_channels set state =#{state} where channel_id =#{channelId}")
    void updateState(@Param("channelId") String channelId, @Param("state") Integer state);

    /**
     * 根据serviceId 进行数据查询
     *
     * @param channelId
     * @return
     */
    @Select("SELECT count(*) FROM video_channels where channel_id = #{channelId}")
    Long selectCountByChannelId(@Param("channelId") String channelId);


    @Select("<script> " +
            "SELECT  remote_address remoteAddress,server_address serverAddress,file_name fileName,state,data,create_date createDate,modify_date modifyDate,channel_id channelId FROM video_channels where state = 0 order by id  desc " +
            "</script>")
    List<VideoChannelDo> selectChannels();


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
