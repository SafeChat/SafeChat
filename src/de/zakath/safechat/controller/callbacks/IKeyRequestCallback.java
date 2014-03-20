package de.zakath.safechat.controller.callbacks;

import java.security.KeyPair;

public interface IKeyRequestCallback {

	void HandleKeyRequestCallback(KeyPair key, int id);

}
