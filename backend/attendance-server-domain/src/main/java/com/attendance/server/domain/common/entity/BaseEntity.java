package com.attendance.server.domain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * 鍏叡瀹炰綋鍩虹被锛岀粺涓€鎵挎帴瀹¤瀛楁銆佸惎鐢ㄧ姸鎬佸拰閫昏緫鍒犻櫎瀛楁銆? * Java 瀛楁淇濈暀棰嗗煙璇箟鍛藉悕锛屾暟鎹簱鍒楃粺涓€璧颁笅鍒掔嚎椋庢牸銆? */
@Getter
@Setter
public class BaseEntity {

    /** 涓婚敭锛岀粺涓€浣跨敤鏁版嵁搴撹嚜澧?ID銆?*/
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 瀹¤瀛楁锛岃褰曞垱寤轰汉鐢ㄦ埛 ID锛屽厑璁镐负绌轰互鍏煎鍒濆鍖栨暟鎹€?*/
    @TableField("created_by_user_id")
    private Long createdByUserId;

    /** 瀹¤瀛楁锛屽啑浣欏垱寤轰汉鍚嶇О锛屼究浜庢帓鏌ュ拰灞曠ず銆?*/
    @TableField("created_by_user_name")
    private String createdByUserName;

    /** 鍒涘缓鏃堕棿鐢辨暟鎹簱缁存姢锛屽疄浣撲晶鍙仛鏄犲皠銆?*/
    @TableField("created_time")
    private LocalDateTime createdTime;

    /** 瀹¤瀛楁锛岃褰曟渶鍚庢洿鏂颁汉鐢ㄦ埛 ID銆?*/
    @TableField("updated_by_user_id")
    private Long updatedByUserId;

    /** 瀹¤瀛楁锛屽啑浣欐渶鍚庢洿鏂颁汉鍚嶇О锛屽噺灏戣仈琛ㄨ鍙栨垚鏈€?*/
    @TableField("updated_by_user_name")
    private String updatedByUserName;

    /** 鏇存柊鏃堕棿鐢辨暟鎹簱鑷姩鏇存柊锛岄伩鍏嶄笟鍔″眰閲嶅璧嬪€笺€?*/
    @TableField("updated_time")
    private LocalDateTime updatedTime;

    /** 涓氬姟鍚敤鏍囪锛岀敤浜庢帶鍒惰褰曟槸鍚﹀彲琚甯镐娇鐢ㄣ€?*/
    @TableField("enabled_flag")
    private Boolean enabledFlag;

    /** 閫昏緫鍒犻櫎鏍囪锛岄厤鍚?MyBatis-Plus 缁熶竴杩囨护宸插垹闄ゆ暟鎹€?*/
    @TableLogic(value = "0", delval = "1")
    @TableField("deleted_flag")
    private Boolean deletedFlag;
}
