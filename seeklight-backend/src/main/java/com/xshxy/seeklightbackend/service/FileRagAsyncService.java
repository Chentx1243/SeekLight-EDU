package com.xshxy.seeklightbackend.service;

import com.xshxy.seeklightbackend.domain.TKbFile;

public interface FileRagAsyncService {
    public void ragStore(TKbFile fileInfo, String objectKey);
}
