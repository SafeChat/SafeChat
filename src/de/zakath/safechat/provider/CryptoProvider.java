package de.zakath.safechat.provider;

import java.security.KeyPair;

import de.zakath.simplecrypt.CombiCrypt;
import de.zakath.simplecrypt.RSA;
import de.zakath.simplecrypt.RSA.VerifyResult;

public class CryptoProvider
{

	private static CombiCrypt _local;
	private static CombiCrypt _server;

	
	public static byte[] signwithLocal(byte[] input)
	{
		initLocal();
		return _local.getRSA().sign(input);
	}

	public static VerifyResult verifywithServer(byte[] input)
	{
		initServer();
		return _server.getRSA().verify(input);
	}

	public static VerifyResult verifywithCustom(byte[] input, KeyPair key)
	{
		RSA _rsa = new RSA(key);
		return _rsa.verify(input);
	}

	public static byte[] encryptToServer(byte[] input)
	{
		initServer();
		return _server.encrypt(input);
	}

	public static byte[] decryptwithLocal( byte[] input)
	{
		initLocal();
		return _local.decrypt(input);
	}

	public static byte[] encryptToCustom(byte[] input, KeyPair key)
	{
		CombiCrypt _cc = new CombiCrypt(key);
		return _cc.encrypt(input);
	}

	private static void initLocal()
	{
		if (_local == null)
		{
			_local = new CombiCrypt(KeyProvider.getOwnKey());
		}
	}

	private static void initServer()
	{
		if (_server == null)
		{
			_server = new CombiCrypt(KeyProvider.getServerKey());
		}
	}

}
