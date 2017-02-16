/**
 * This code generated by CodeGenerator<ptma@163.com>
 */
package com.zz.zwj.controller;

import java.util.Date;


/**
 * <p>实体类</p>
 * <p>Table: basic_activity_pic - 活动图片</p>
 *
 * @since 2017-02-16 04:39:15
 */
public class BasicActivityPicController {

    /** pic_id - 主键ID */
    private Long picId;

    /** activities_id - 活动ID */
    private Long activitiesId;

    /** pic_type - 图片类型：1,热门活动图_Android 2,热门活动图_iPhone 3,首页活动图_Android 4,首页活动图_iPhone 5,活动icon_Android 6,活动icon_iPhone */
    private Byte picType;

    /** pic_url - 图片地址 */
    private String picUrl;

    /** pic_width - 图片宽度 */
    private Long picWidth;

    /** pic_height - 图片长度 */
    private Long picHeight;

    /** pic_size - 图片大小，单位KB */
    private Long picSize;

    /** create_time - 创建时间 */
    private Date createTime;

    /** update_time - 更新时间 */
    private Date updateTime;

    /** targe_url - 目标网址 */
    private String targeUrl;

    /** pic_description - 图片描述 */
    private String picDescription;

    /** add_user_id - 添加管理员ID */
    private Long addUserId;

    /** update_user_id - 更新管理员ID */
    private Long updateUserId;

    /** status - 状态： 0-未上架 1-已上架 */
    private Byte status;

    /** begin_time - 开始时间 */
    private Date beginTime;

    /** end_time - 结束时间 */
    private Date endTime;


    public Long getPicId(){
        return this.picId;
    }
    public void setPicId(Long picId){
        this.picId = picId;
    }

    public Long getActivitiesId(){
        return this.activitiesId;
    }
    public void setActivitiesId(Long activitiesId){
        this.activitiesId = activitiesId;
    }

    public Byte getPicType(){
        return this.picType;
    }
    public void setPicType(Byte picType){
        this.picType = picType;
    }

    public String getPicUrl(){
        return this.picUrl;
    }
    public void setPicUrl(String picUrl){
        this.picUrl = picUrl;
    }

    public Long getPicWidth(){
        return this.picWidth;
    }
    public void setPicWidth(Long picWidth){
        this.picWidth = picWidth;
    }

    public Long getPicHeight(){
        return this.picHeight;
    }
    public void setPicHeight(Long picHeight){
        this.picHeight = picHeight;
    }

    public Long getPicSize(){
        return this.picSize;
    }
    public void setPicSize(Long picSize){
        this.picSize = picSize;
    }

    public Date getCreateTime(){
        return this.createTime;
    }
    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    public Date getUpdateTime(){
        return this.updateTime;
    }
    public void setUpdateTime(Date updateTime){
        this.updateTime = updateTime;
    }

    public String getTargeUrl(){
        return this.targeUrl;
    }
    public void setTargeUrl(String targeUrl){
        this.targeUrl = targeUrl;
    }

    public String getPicDescription(){
        return this.picDescription;
    }
    public void setPicDescription(String picDescription){
        this.picDescription = picDescription;
    }

    public Long getAddUserId(){
        return this.addUserId;
    }
    public void setAddUserId(Long addUserId){
        this.addUserId = addUserId;
    }

    public Long getUpdateUserId(){
        return this.updateUserId;
    }
    public void setUpdateUserId(Long updateUserId){
        this.updateUserId = updateUserId;
    }

    public Byte getStatus(){
        return this.status;
    }
    public void setStatus(Byte status){
        this.status = status;
    }

    public Date getBeginTime(){
        return this.beginTime;
    }
    public void setBeginTime(Date beginTime){
        this.beginTime = beginTime;
    }

    public Date getEndTime(){
        return this.endTime;
    }
    public void setEndTime(Date endTime){
        this.endTime = endTime;
    }
}