# Cryptography-Java

/**
 * 
 * @author Smit
 * Steps:
 * 1) openssl genrsa -des3 -out private.pem 2048
 * 2) openssl rsa -in private.pem -outform PEM -pubout -out public.pem
 * # convert private Key to PKCS#8 format (so Java can read it)
 * 3) openssl pkcs8 -topk8 -inform PEM -outform DER -in private.pem -out private_key.der -nocrypt
 * 4) openssl rsa -in private.pem -pubout -outform DER -out public_key.der
 * 
 * Ref: https://blog.jonm.dev/posts/rsa-public-key-cryptography-in-java/
 */
