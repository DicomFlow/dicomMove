package br.ufpb.dicomflow.bean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufpb.dicomflow.util.MD5;


@Entity
@Table(name="files")
public class File extends AbstractPersistence {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 202100461015580129L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="pk",unique=true)
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="instance_fk")
	private Instance instance;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="filesystem_fk")
	private FileSystem fileSystem;
	
	@Column(name="created_time")
    private Date createdTime;
	
	@Column(name="md5_check_time")
    private Date timeOfLastMd5Check;
	
	@Column(name="filepath")
    private String filePath;
	
	@Column(name="file_tsuid")
    private String fileTsuid;
	
	@Column(name="file_md5")
    private String fileMd5Field;
	
	@Column(name="file_status")
    private Integer fileStatus;
	
	@Column(name="file_size")
    private Long fileSize;
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}

	public FileSystem getFileSystem() {
		return fileSystem;
	}

	public void setFileSystem(FileSystem fileSystem) {
		this.fileSystem = fileSystem;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getTimeOfLastMd5Check() {
		return timeOfLastMd5Check;
	}

	public void setTimeOfLastMd5Check(Date timeOfLastMd5Check) {
		this.timeOfLastMd5Check = timeOfLastMd5Check;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileTsuid() {
		return fileTsuid;
	}

	public void setFileTsuid(String fileTsuid) {
		this.fileTsuid = fileTsuid;
	}

	public String getFileMd5Field() {
		return fileMd5Field;
	}

	public void setFileMd5Field(String fileMd5Field) {
		this.fileMd5Field = fileMd5Field;
	}

	public Integer getFileStatus() {
		return fileStatus;
	}

	public void setFileStatus(Integer fileStatus) {
		this.fileStatus = fileStatus;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	
	/**
	 * MD5 checksum in binary format
	 * 
	 */
	public byte[] getFileMd5() {
		return MD5.toBytes(getFileMd5Field());
	}
	
	 
	public void setFileMd5(byte[] md5) {
		setFileMd5Field(MD5.toString(md5));
	}
	
	public boolean isRedundant() {
		Instance inst = getInstance();
		return inst == null || inst.getFiles().size() > 1;
	}
	
}
