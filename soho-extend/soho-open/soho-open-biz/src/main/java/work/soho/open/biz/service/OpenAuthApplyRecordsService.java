package work.soho.open.biz.service;

import work.soho.open.biz.domain.OpenAuthApplyRecords;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OpenAuthApplyRecordsService extends IService<OpenAuthApplyRecords> {
    // 审核通过
    Boolean audit(OpenAuthApplyRecords openAuthApplyRecords);
    // 审核拒绝
    Boolean reject(OpenAuthApplyRecords openAuthApplyRecords, String reason);
}