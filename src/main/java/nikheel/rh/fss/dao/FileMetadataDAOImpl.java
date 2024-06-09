package nikheel.rh.fss.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import nikheel.rh.fss.domain.FileMetadata;

@Repository
public class FileMetadataDAOImpl implements FileMetadataDAOAPI {
	@Autowired
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public FileMetadataDAOImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void save(FileMetadata metadata) {
		String sql = "INSERT INTO file_metadata (file_name, size, uploaded_date, checksum) VALUES (?, ?, ?, ?)";
		jdbcTemplate.update(sql, metadata.getFileName(), metadata.getSize(), metadata.getUploadedDate(), metadata.getChecksum());
	}

	@Override
	public List<FileMetadata> findAll() {
		String sql = "SELECT * FROM file_metadata";
		return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFileMetadata(rs));
	}

	@Override
	public List<FileMetadata> findByChecksum(String checksum) {
		String sql = "SELECT * FROM file_metadata WHERE checksum = ?";
		try {
			List<FileMetadata> fileMetadataList = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFileMetadata(rs), new Object[] { checksum });
			return fileMetadataList;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<String> listAllFileNames() {
		String sql = "SELECT file_name FROM file_metadata";
		return jdbcTemplate.queryForList(sql, String.class);
	}

	@Override
	public boolean deleteByFileName(String fileName) {
		String sql = "DELETE FROM file_metadata WHERE file_name = ?";
		int output = jdbcTemplate.update(sql, fileName);
		return output > 0 ? true : false;
	}

	@Override
	public FileMetadata findByFileName(String fileName) {
		String sql = "SELECT * FROM file_metadata WHERE file_name = ?";
		List<FileMetadata> results = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFileMetadata(rs), new Object[] { fileName });
		return results.isEmpty() ? null : results.get(0);
	}

	private FileMetadata mapRowToFileMetadata(ResultSet rs) throws SQLException {
		FileMetadata metadata = new FileMetadata();
		metadata.setFileName(rs.getString("file_name"));
		metadata.setSize(rs.getLong("size"));
		metadata.setUploadedDate(rs.getObject("uploaded_date", LocalDateTime.class));
		metadata.setChecksum(rs.getString("checksum"));
		return metadata;
	}
}
