package org.net.perorin.bastet.util

import java.security.MessageDigest

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

import org.apache.commons.lang3.RandomStringUtils

import javafx.scene.control.Tooltip

class Util {

	static def getResourceURL(def resource){
		return Thread.currentThread().getContextClassLoader().getResource(resource)
	}

	static def getResourceStr(def resource){
		return Thread.currentThread().getContextClassLoader().getResource(resource).toString()
	}

	static def getResourceAsStream(def resource){
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)
	}

	static def getClassName() {
		return Thread.currentThread().getStackTrace()[2].getClassName()
	}

	/**
	 * MACアドレス取得
	 *
	 * @return MACアドレス
	 */
	static def getMacAddress() {

		// ネットワークインターフェース取得
		Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
		String s;
		while(nics.hasMoreElements()){
			NetworkInterface nic = nics.nextElement();

			// MACアドレス取得
			byte[] hardwareAddress = nic.getHardwareAddress();
			if(hardwareAddress != null){
				for(byte b : hardwareAddress){
					if(String.format("%02X ", b) != "") {
						s += String.format("%02X", b)
					}
				}
			}
		}
		return s
	}

	/**
	 * 暗号化
	 *
	 * @param salt ソルト
	 * @param message 暗号化対象
	 * @return 暗号化後
	 */
	static def encrypt(String salt, String message) {

		// 暗号鍵取得
		byte[] key = Util.getKey().getBytes();
		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		key = sha.digest(key);
		key = Arrays.copyOf(key, 32);
		SecretKeySpec keyObj = new SecretKeySpec(key, "AES");
		IvParameterSpec ivObj = new IvParameterSpec(Arrays.copyOf(salt.getBytes(), 16));
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.ENCRYPT_MODE, keyObj, ivObj);
		byte[] byteCipherText = cipher.doFinal(message.getBytes());
		return Base64.getEncoder().encodeToString(byteCipherText);
	}

	/**
	 * 複合化
	 *
	 * @param salt ソルト
	 * @param message 暗号化文字列
	 * @return 複合化後
	 */
	static def decrypt(String salt, String cryptText) {
		byte[] key = Util.getKey().getBytes();
		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		key = sha.digest(key);
		key = Arrays.copyOf(key, 32);
		SecretKeySpec keyObj = new SecretKeySpec(key, "AES");
		IvParameterSpec ivObj = new IvParameterSpec(Arrays.copyOf(salt.getBytes(), 16));
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.DECRYPT_MODE, keyObj, ivObj);
		byte[] cipherBytes = Base64.getDecoder().decode(cryptText);
		byte[] byteDecryptedText = cipher.doFinal(cipherBytes);
		return new String(byteDecryptedText);
	}

	/**
	 * 暗号鍵取得
	 *
	 * @return 暗号鍵
	 */
	static def getKey() {

		// 鍵ファイル取得
		File keyFile = new File(SqlUtil.getParameter("AES-Key-Path").Para1)

		// MACアドレスのハッシュ値取得
		int mac = Math.abs(Util.getMacAddress().hashCode())

		// 剰余を用いて鍵の中から使用する範囲を決定
		int start
		if(keyFile.text.length() > mac) {
			start = keyFile.text.length() % mac
		}else {
			start = mac % keyFile.text.length()
		}
		int end
		if(start + 32 < keyFile.text.length()) {
			end = start + 32
		}else {
			end = start
			start = start - 32
		}

		return keyFile.text.substring(start, end);
	}

	static def createKey(File path) {

		// 鍵ファイル生成
		File keyFile = new File(path.getAbsolutePath() + File.separator + ".bastet_key")
		keyFile.createNewFile()
		// ランダム文字列生成
		keyFile.text = RandomStringUtils.randomAlphabetic(1024 * 1024)
		SqlUtil.setParameter("AES-Key-Path", [keyFile.getAbsolutePath().replace('\\', '/')])
	}

	static def config = null
	static def getConfig() {
		if(config == null) {
			config = new ConfigSlurper().parse(new File("Config.groovy").toURI().toURL())
		}
		return config
	}

	static def execPowerShell(def ps1, def args) {
		def msg = []
		def proc = "powershell -NoProfile -ExecutionPolicy Unrestricted .\\${ps1} ${args.join(" ")}".execute()
		Thread.start {
			proc.in.eachLine {
				if(it.trim() != "") {
					msg << it.trim()
				}
			}
		}
		proc.waitFor()
		return msg.last()
	}

	static def addTooltipToColumnCells(def column) {

		def existingCellFactory = column.getCellFactory()
		column.setCellFactory({c ->
			def cell = existingCellFactory.call(c)

			Tooltip tooltip = new Tooltip()
			tooltip.textProperty().bind(cell.itemProperty().asString())

			cell.setTooltip(tooltip)
			return cell
		})
	}

	static def redirectFollowingDownload( String url, String filename ) {
		new URL(url).openConnection().with { conn ->
			conn.instanceFollowRedirects = false
			url = conn.getHeaderField("Location")
			if( !url ) {
				new File( filename ).withOutputStream { out ->
					conn.inputStream.with { inp ->
						out << inp
						inp.close()
					}
				}
			}
		}
	}
}


