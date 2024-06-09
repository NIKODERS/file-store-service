package nikheel.rh.fss.controller;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import nikheel.rh.fss.domain.FileMetadata;
import nikheel.rh.fss.domain.SortOrder;
import nikheel.rh.fss.service.FileStoreServiceAPI;

@RestController
@RequestMapping("/fss")
public class FileStoreController {
	@Autowired
	private FileStoreServiceAPI fileStoreService;
	private static Logger LOGGER = LoggerFactory.getLogger(FileStoreController.class);

	@GetMapping(value = "/testHeartbeat", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> checkHeartBeat() {
		LOGGER.info("Heart Beat Test done at:" + Calendar.getInstance().getTime());
		String response = "Service is up and running !!";
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/addFiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addFiles(@RequestParam("files") MultipartFile[] files) {
		List<String> uploadedFiles = fileStoreService.addFiles(files);
		return ResponseEntity.ok().body("Uploaded files: " + uploadedFiles);
	}

	@GetMapping(value = "/exists/{checksum}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> checkFileExistence(@PathVariable String checksum) {
		boolean exists = fileStoreService.doesFileExist(checksum);
		String response = "File's checksum available";
		if (exists) {
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("File NOT Available", HttpStatus.OK);
		}
	}

	@GetMapping(value = "/multiExists", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> checkFilesExistence(@RequestParam List<String> checksums) {
		List<String> existingChecksums = fileStoreService.doesFilesExist(checksums);
		if (!existingChecksums.isEmpty()) {
			return ResponseEntity.ok(existingChecksums);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(existingChecksums);
		}
	}

	@GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> listFiles() {
		List<String> fileNames = fileStoreService.listFiles();
		return ResponseEntity.ok(fileNames);
	}

	@PostMapping(value = "/addMetadata", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addFileMetadata(@RequestBody FileMetadata metadata) {
		fileStoreService.addFileMetadata(metadata);
		return new ResponseEntity<>("File metadata added successfully", HttpStatus.OK);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<String> removeFile(@RequestParam("fileName") String fileName) {
		fileStoreService.removeFile(fileName);
		return new ResponseEntity<>("File removed successfully", HttpStatus.OK);
	}

	@PostMapping("/update")
	public ResponseEntity<String> updateFile(@RequestParam("file") MultipartFile file) {
		String outputMessage = fileStoreService.updateFile(file);
		return ResponseEntity.status(HttpStatus.OK).body(outputMessage);
	}

	@GetMapping("/getWordCount")
	public ResponseEntity<Map<String, Integer>> getWordCount() {
		Map<String, Integer> wcMap = fileStoreService.wordCount();
		return new ResponseEntity<>(wcMap, HttpStatus.OK);
	}

	@GetMapping("/getFrequentwords")
	public ResponseEntity<Map<String, Integer>> getFrequentWords(@RequestParam(name = "limit", defaultValue = "10") int limit,
			@RequestParam(name = "order", defaultValue = "desc") SortOrder order) {
		Map<String, Integer> frequentWords = fileStoreService.getFrequentWords(limit, order);
		return new ResponseEntity<>(frequentWords, HttpStatus.OK);
	}
}
