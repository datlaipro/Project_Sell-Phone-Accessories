// ex: src/main/java/.../security/TokenReuseException.java
package com.github.datlaipro.shop.security;
public class TokenReuseException extends RuntimeException {
  public TokenReuseException(String msg) { super(msg); }
}
