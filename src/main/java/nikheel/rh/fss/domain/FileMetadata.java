package nikheel.rh.fss.domain;

import java.time.LocalDateTime;

public class FileMetadata {

	private String fileName;
	private Long size;
	private LocalDateTime uploadedDate;
	private String checksum;

	public FileMetadata(String fileName, Long size, LocalDateTime uploadedDate, String checksum) {
		this.fileName = fileName;
		this.size = size;
		this.uploadedDate = uploadedDate;
		this.checksum = checksum;
	}

	public FileMetadata() {
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public LocalDateTime getUploadedDate() {
		return uploadedDate;
	}

	public void setUploadedDate(LocalDateTime uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
}
