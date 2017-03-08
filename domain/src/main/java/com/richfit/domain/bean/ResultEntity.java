package com.richfit.domain.bean;

import java.util.Map;

/**
 * Created by monday on 2016/9/22.
 */

public class ResultEntity {
    //任务id
    public int taskId;
    //    验收人ID（当前登陆用户的userId）
    public String inspectionPerson;
    //验收结果
    public String inspectionResult;
    //是否修改
    public String modifyFlag;
    public int inspectionType;
    //    备注
    public String remark;
    public String imageName;
    //    照片的后缀名
    public String suffix;
    //    验收单的头ID
    public String bizHeadId;
    public String supplierId;
    //    验收行ID
    public String bizLineId;
    //    拍照环节（1.验收  2.入库  3.出库）
    public String bizPart;
    //    手持上图片的完整路径+名称
    public String imagePath;
    //    当前登陆用户的userId
    // 特殊库存标识 N:普通 E:订单 W:寄售 D:带储代销 K:供应商寄售
    public String specialInvFlag;
    public String createdBy;
    //    创建日期（照片拍照保存的时间 Date格式）
    public String imageDate;
    //    参考单据的ID
    public String refCodeId;
    public String refCode;
    //单据行号
    public String refLineNum;
    //    过账日期
    public String voucherDate;
    //拍照类型
    public int fileType;
    //     单据类型
    public String refType;
    //业务类型
    public String businessType;
    //     移动类型
    public String moveType;
    //     用户id
    public String userId;
    //     参考行ID
    public String refLineId;
    //     工厂ID
    public String workId;
    //     库存地点ID
    public String invId;
    //     物料ID
    public String materialId;
    public String materialNum;
    //     仓位
    public String location;
    public String locationId;
    //     批次
    public String batchFlag;
    //     用户录入的数量
    public String quantity;
    //单价
    public String price;

    //盘点id
    public String checkId;

    public String invType;
    //接收工厂
    public String recWorkId;
    //接收库位
    public String recInvId;
    //接收仓位
    public String recLocation;
    //接收批次
    public String recBatchFlag;

    public Map<String, Object> mapExHead;// 头扩展字段
    public Map<String, Object> mapExLine;// 行扩展字段
    public Map<String, Object> mapExLocation;// 仓位扩展字段
    public String inspectionQuantity;// 送检数
    public String manufacturer;//制造商
    public String randomQuantity;// 抽检
    public String rustQuantity;// 锈蚀
    public String damagedQuantity;// 损坏
    public String badQuantity;// 变质
    public String otherQuantity;// 其他
    public String qualifiedQuantity;//质检数量
    public String sapPackage;// 包装情况 1.完好/2.散箱/3.包/4.件/5.无包装/6.其他
    public String qmNum;// 质检单号
    public String certificate;// 合格证
    public String instructions;// 说明书
    public String qmCertificate;// 质检证书
    public String claimNum;// 索赔单号
    public String specialInvNum;
    //成本中心
    public String costCenter;
    //项目编号
    public String projectNum;
    public String checkLineId;
    //物料凭证
    public String refDoc;
    public Integer refDocItem;
    //退货交货数量
    public String returnQuantity;
    //检验批数量
    public String insLot;
    //移动原因说明
    public String moveCauseDesc;
    //项目文本
    public String projectText;
}
