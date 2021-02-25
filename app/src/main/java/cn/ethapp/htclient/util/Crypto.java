package cn.ethapp.htclient.util;

import android.text.TextUtils;

import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.MnemonicUtils;

import java.math.BigInteger;

import cn.ethapp.htclient.bean.eth.SchemeAddress;
import cn.ethapp.htclient.eth.SecureRandomUtils;

import static org.web3j.crypto.Bip32ECKeyPair.HARDENED_BIT;
import static org.web3j.crypto.Hash.sha256;

/*
 * Created by apple on 2020-09-22
 */
public class Crypto {

    public static String generateMnemonic() {
        byte[] initialEntropy = new byte[16];
        SecureRandomUtils.secureRandom().nextBytes(initialEntropy);
        return MnemonicUtils.generateMnemonic(initialEntropy);
    }

    public static String generatePrivateKeyFromMnemonic(String mnemonic) {
        Credentials credentials = loadBip44Credentials(null, mnemonic, false);
        String pri = credentials.getEcKeyPair().getPrivateKey().toString(16);
        return pri;
    }

    public static Credentials generateCredentials(String mnemonic) {
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, "");
        return Credentials.create(ECKeyPair.create(sha256(seed)));
    }

    public static String getWalletAddress(String mn) {
        return generateCredentials(mn).getAddress();
    }

    public static String generateAddressFromPriv(String pri16) {
        if (TextUtils.isEmpty(pri16)){
            return null;
        }
        BigInteger pri = new BigInteger(pri16, 16);
        ECKeyPair pair = ECKeyPair.create(pri);
        Credentials credentials = Credentials.create(pair);

        return credentials.getAddress();
    }

    public static String getShortWalletAddress(String address, int head_, int end_) {
        if (!TextUtils.isEmpty(address)){
            String head = address.substring(0, head_);
            String end = address.substring(address.length() - end_, address.length());
            return String.valueOf(head + "..." + end);
        }
        return address;
    }

    public static Credentials getCredentials() {
        BigInteger pri = new BigInteger(EthGlobal.getInstance().getNowPrivateKey(), 16);
        ECKeyPair pair = ECKeyPair.create(pri);
        return Credentials.create(pair);
    }

    public static Bip32ECKeyPair generateBip44KeyPair(Bip32ECKeyPair master, boolean testNet) {
        if (testNet) {
            // /m/44'/0'/0
            final int[] path = {44 | HARDENED_BIT, 0 | HARDENED_BIT, 0 | HARDENED_BIT, 0, 0};
            return Bip32ECKeyPair.deriveKeyPair(master, path);
        } else {
            // m/44'/60'/0'/0
            final int[] path = {44 | HARDENED_BIT, 60 | HARDENED_BIT, 0 | HARDENED_BIT, 0, 0};
            return Bip32ECKeyPair.deriveKeyPair(master, path);
        }
    }

    public static Credentials loadBip44Credentials(
            String password, String mnemonic, boolean testNet) {
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, password);
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
        System.out.println(masterKeypair.getChildNumber());
        System.out.println(masterKeypair.getPrivateKey().toString(16));
        Bip32ECKeyPair bip44Keypair = generateBip44KeyPair(masterKeypair, testNet);
        System.out.println(bip44Keypair.getPrivateKey().toString(16));
        return Credentials.create(bip44Keypair);
    }

    public static String genEthAddress(String address, String contractAddress, String decimal, String value) {
        SchemeAddress schemeAddress = new SchemeAddress("ethereum", address, contractAddress, decimal, value);
        return schemeAddress.getSchemeAddress();
    }

    public static SchemeAddress parsSchemeAddress(String mSAddress) {
        SchemeAddress schemeAddress = new SchemeAddress();
        if (TextUtils.isEmpty(mSAddress)){
            return null;
        }

        if (!mSAddress.contains(":")){
            return null;
        }
        String[] array = mSAddress.split(":");
        if (array.length != 2){
            return null;
        }
        schemeAddress.setName(array[0]);
        if (!array[1].contains("?")){
            return null;
        }
        String[] contents = array[1].split("\\?");
        if (contents.length != 2){
            return null;
        }
        schemeAddress.setTo(contents[0]);
        if (!contents[1].contains("&")){
            return null;
        }
        String[] params = contents[1].split("&");
//        int count = params.length;
        for (String param : params) {
            if (param.contains("=")) {
                String[] kvs = param.split("=");
                if (kvs.length == 2) {
                    String key = kvs[0];
                    if (key.equals("decimal")) {
                        schemeAddress.setDecimal(kvs[1]);
                    } else if (key.equals("contractAddress")) {
                        schemeAddress.setContractAddress(kvs[1]);
                    } else if (key.equals("value")) {
                        schemeAddress.setValue(kvs[1]);
                    }
                }
            }
        }
        return schemeAddress;
    }


}
