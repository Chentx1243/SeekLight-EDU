package com.xshxy.seeklightbackend.service;

import com.xshxy.seeklightbackend.domain.TKbFile;

public interface FileRagAsyncService {
     void ragStore(TKbFile fileInfo, String objectKey);
}
