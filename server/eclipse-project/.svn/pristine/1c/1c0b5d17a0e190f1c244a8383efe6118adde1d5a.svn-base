package us.freeandfair.corla.json;

import us.freeandfair.corla.model.UploadedFile;
import us.freeandfair.corla.csv.Result;
import us.freeandfair.corla.util.SuppressFBWarnings;

@SuppressFBWarnings(value = {"URF_UNREAD_FIELD"}, justification = "Field is read by Gson.")
public class UploadedFileDTO {
  private Integer approximateRecordCount;
  private Long countyId;
  private String fileName;
  private String hash;
  private Long id;
  private Result result;
  private String status;
  private Long size;
  private String timestamp;

  public UploadedFileDTO(final UploadedFile uploadedFile) {
    this.approximateRecordCount = uploadedFile.approximateRecordCount();
    this.countyId = uploadedFile.county().id();
    this.fileName = uploadedFile.filename();
    this.hash = uploadedFile.getHash();
    this.id = uploadedFile.id();
    this.result = uploadedFile.getResult();
    this.size = uploadedFile.size();
    this.status = uploadedFile.getStatus().toString();
    this.timestamp = uploadedFile.timestamp().toString();
  }

  public Long getFileId() {
    return this.id;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(final String status ) {
    this.status = status;
  }

  public void setResult(final Result result) {
    this.result = result;
  }

  public Result getResult() {
    return this.result;
  }

  public Long getCountyId() {
    return this.countyId;
  }

  public void setCountyId(final Long countyId) {
    this.countyId = countyId;
  }
}
