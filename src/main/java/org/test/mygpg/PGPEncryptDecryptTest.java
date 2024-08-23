package org.test.mygpg;

public class PGPEncryptDecryptTest {

    public static String armorPrivateKey ="-----BEGIN PGP PRIVATE KEY BLOCK-----\n" +
            "Version: BCPG v1.59\n" +
            "\n" +
            "lQPGBGbEZnQDCADIWVLTTSgK7hyEVmnlLkkIZDgr91RgSWezO1gYwLB7n0wt3jfd\n" +
            "voPSxSMQWWp9Na9j7fMfFwAXOrOfM5NNmxmKKP269n9wNxfKv5UbCmcs4Ydz7Kjj\n" +
            "mBX+7OgaGtjbkdgXGFFDmSIkADa2f65+lj3y6q7EfmjH5/+Q0FSebSlxtXrNpjI0\n" +
            "h7CH8X8H2vF+ecaii3x3SaX29mAte7hwKdJj3G+1c+tLSb88blUyn7r9F+4cTMQq\n" +
            "1AC0kIUIwe/lB5MRpXl1oiEb+nfGhUBr30JvkAjQtTM1RjcI3a665ZsQC/vhoVXs\n" +
            "t8EWbXWYQvJ01bSFNZrlOJN5inF7ez0ICG/PABEBAAH+CQMIy5u2eQE9oaXA224T\n" +
            "I0fzj+3pLBp/t63j+ycHTxZKYTo8Uy9guY0E+RABeEZGyk+w7vbdFrhthVfMxEUk\n" +
            "7le0mnsbhzDBR2ddRkK3tENB47XqfHbKq8Mw6zx8huRvLdXAUruqbMY/v8Z2TS4l\n" +
            "3SoreacgKA9QERn1xFEgncXTrW4uMCeLV6gNzxdTShtOE2F6+HKbzpJ6OUBWFR2O\n" +
            "XR0tH1iSqVroLjFet7h4p48kRL3GGgAUrjOrM4HBFRp5AdWYPvTrlGLoi8XkMVnN\n" +
            "IdqSepOKaJtH0+2g9tPQfILhpCbdiH/BcaELzrh6gxbzY/zetVIM6GYRrxA6Fd8W\n" +
            "7JjThSyRWNsSHx+PqoiqlFZE6BfE4ekyTf3Vg6RSu2FVZa75sM9q+b7510n7+W6i\n" +
            "wLvxxBGAdXfrqVjhHrFBlaliS/sBIkoHiBm4lG4e+6fuLNkfWbZqaz6++eCs5ULo\n" +
            "ToSMZrwt3z7RkvUZgfXdv9kq0te4dOVQH+qilPELrjlGjluW4kEfO0f6JRKF02Bj\n" +
            "+afkPC9DiXNwPa/igoft7OBY48EPnlO/+3gJmIZo1byMmYdscrQr7Tg0qTxntNrL\n" +
            "/184yQp37ktPCVy7eBrINFIb0N+EM50ktceELSR5/K08p8PAIssmAi+dgaHr6j2b\n" +
            "uaQCg/x0pxby3Hoheu3sfQ6Q/inBrF8b/hFI4FBgcBNfz5K+OlilfE4aH68ln/cp\n" +
            "CJpbs4qSUIaJFioGCmOQ1r3anfxsDc1V21/1HFiuuSoRuD0EhHRObk4ddWUdpc0x\n" +
            "frQH5ydPTsezCIHl5a1ZSXp9OvuGg1ljlGruaOYMFeCTPdFjHZFY4ZNdf39J4uWL\n" +
            "5MDds+vVyv4/t25FK1mPdSBeOYjJWlyDnvVWbf6ruf886MtOnA9JQ3hiBnn5DXl1\n" +
            "VFd/8mwkNZfftBtCcmFkIFlhbiA8YWFyMGZ6ekB0ZXN0LmNvbT6JASgEEwMCABIF\n" +
            "AmbEZnUCGwMCCwkCFQoCHgEACgkQ4i55eajsTmJj9gf+LBIF/nKH60HLPLYQDxX4\n" +
            "vuYo3tarXOsy3CzGOC+h3cN1D6IdBuRWQdLETR6C/wVmW3RNBt5KPQH2TByhN41B\n" +
            "AIp1+k0GQvpFWSBmYd+KD58n82lkBEjHsUwyW2IrqAAUPtfM8xWzSPRgHV0f8VPB\n" +
            "ulQEGa2Tlq7QkDHerUFICFZI+ByAYXlUGpKhtFlGKar1W1mi6O3nvKdZCbH1ZyCf\n" +
            "99MtkERcXdV0bohVqgrxmHjuoyLIWTpMHrcE73o47ybybXLl71p6tx5Rds0KnchC\n" +
            "fcomcaRa0xxkt/GdktoDg0H21uzE5diYdrEcodAT9zovDPIs4IsRVhtv19JV3qb1\n" +
            "VZ0DxgRmxGZ0AQgA0bKHzvtl99Fjz6QbVWVKLvWs48DJLpU1rWN5dCtqKYhkqkUv\n" +
            "onb0VTDkDB/Zpu204x16x8/11zht3fHDut5QDyGXJLvlTn3/4rHvGvn5DLpXpNRg\n" +
            "1nXGBBEM5Z4MhZWedNm9L0Qpx7i9ffJlViYg1cSCgr0WIkWEv6Gxe+7vHZLZk4H5\n" +
            "EtF9CtkHVh4IJV+wtqxXOpPDn7KZ9mR2i1/6iM9u+NiCvAaXQDOhzUapa2yaB1aD\n" +
            "Zla2mdQxGGTTJDxG3zh4IFTpbRF1q1aMX1wJ0IdK35IOFltycmNUBRMYUPYj4aAT\n" +
            "Ztr53U/W+/PP3QALjnOiA4fABa/cIoyzBS5FYQARAQAB/gkDCMubtnkBPaGlwFaQ\n" +
            "Me1AtIe760Ck+I3mmYM8C0fpqMEGBipARb/CFLu9Yfz7DuWwMNmRDRDCA6a41zFL\n" +
            "wx1KMOOJ3L6wKLzOdl7unHRJ8IZ6+xqL9inKuxuasWX9RdPByCUZHzvDJoDAQNA2\n" +
            "Ka7LOTtd2icovnqdmq9Mx4tCCFeEXP63nR39hCjo8F+mJ5L5dV//UT2TZ8ElPP2l\n" +
            "zhXxnbI7R6NpCJO0b89AgPn6wGAM5c5wieiy3djaBeYVFwe5XfdhR1wPTPRN4tNz\n" +
            "bRX0genoV6JivQKyJDGRUSZsct3UZYxCKaWnDMU73Q9k4MXicfMEhBcPE3H1XPhh\n" +
            "z1qVQctzQsHrPzhWVFzIOaiutdBl7FbbAa57sNRQitiTpmtnnQ5KBbLZpW64Y/Zh\n" +
            "iYtsL6Z361xDUZn9xh+dwb0irz2G4hiczd6Be170ttYoh5HVgNjJf/4S9tyud48/\n" +
            "3JDBm31O26ec7t4CUypWMpP89z0iYmK5Gv7c56gN4NJGw7dzcZ7fRCnhd7lqC1xY\n" +
            "5wOsHSAu9xJGSWwnu/bb18A3e5191B12Vf2hsEaKmt0Uy4ijA2pn/QuaqFj/39hd\n" +
            "+4jCmm5uiQS9K4yJvXY1WZ3OCJ2QQZWBjVg5UIj4zztQrZkJg2lgahxsWcTS2uo9\n" +
            "f3YrHuracT2fpKcRk4kf9kCeeXRptYCo+iuaDMGRclzZ9bPxsgWCR9Lt/aBcGdFi\n" +
            "DNI1j5Cj2iqmB3/6PlUEYCEatQ41j/PbTdGCj065HcqfZvsqftN58EhtCur15VC1\n" +
            "IBQpYk3rHk+nhxqW9dcLqEmZlTCs95SKeBarFe1Eg761VyKkk3Mx/4gggAATaPJU\n" +
            "4vaiHK3KFCg0YZQqRSkNYcKm2Vjf/+jWUsZofRfAi4AZBaqUu0DaKso/7AvjBDGl\n" +
            "CUcbMnrklflYlokBHwQYAwIACQUCZsRmdQIbDAAKCRDiLnl5qOxOYjgoB/99P6VP\n" +
            "faRtUZEiurv9g0rVqx5nb6BcLtK5jb3/PfqObwztDGRyrBVB5ruqGc0Zlv1mIKiF\n" +
            "KArn93PrpMZxU+BKUFtCJwCVpyb0towSWcrpPXhDqCN66TMdgfJ44NBnEVUQWHXA\n" +
            "MI8haBHgHLHJ9KVRQnLQ2SwQyKTXK038h+ltEBg7PkszL1P/Pfl1aqrca0U6ZQ7x\n" +
            "LOMkGUS/EvlwMjP/6WeI+eM3dmcy/Uvq8UG6rrUsDwTCk4YmXuH1EelQuTSQSxEK\n" +
            "IWcNojZOy0AyDOZ0s6BIPf4kfgAMShOsnjyX1O8Ow7DxJOMBaQI6kKp8pkzTXgy5\n" +
            "eoLIVXbClgYhLv2t\n" +
            "=cRw2\n" +
            "-----END PGP PRIVATE KEY BLOCK-----";

    public static String armorPublicKey = "-----BEGIN PGP PUBLIC KEY BLOCK-----\n" +
            "Version: BCPG v1.59\n" +
            "\n" +
            "mQENBGbEZnQDCADIWVLTTSgK7hyEVmnlLkkIZDgr91RgSWezO1gYwLB7n0wt3jfd\n" +
            "voPSxSMQWWp9Na9j7fMfFwAXOrOfM5NNmxmKKP269n9wNxfKv5UbCmcs4Ydz7Kjj\n" +
            "mBX+7OgaGtjbkdgXGFFDmSIkADa2f65+lj3y6q7EfmjH5/+Q0FSebSlxtXrNpjI0\n" +
            "h7CH8X8H2vF+ecaii3x3SaX29mAte7hwKdJj3G+1c+tLSb88blUyn7r9F+4cTMQq\n" +
            "1AC0kIUIwe/lB5MRpXl1oiEb+nfGhUBr30JvkAjQtTM1RjcI3a665ZsQC/vhoVXs\n" +
            "t8EWbXWYQvJ01bSFNZrlOJN5inF7ez0ICG/PABEBAAG0G0JyYWQgWWFuIDxhYXIw\n" +
            "Znp6QHRlc3QuY29tPokBKAQTAwIAEgUCZsRmdQIbAwILCQIVCgIeAQAKCRDiLnl5\n" +
            "qOxOYmP2B/4sEgX+cofrQcs8thAPFfi+5ije1qtc6zLcLMY4L6Hdw3UPoh0G5FZB\n" +
            "0sRNHoL/BWZbdE0G3ko9AfZMHKE3jUEAinX6TQZC+kVZIGZh34oPnyfzaWQESMex\n" +
            "TDJbYiuoABQ+18zzFbNI9GAdXR/xU8G6VAQZrZOWrtCQMd6tQUgIVkj4HIBheVQa\n" +
            "kqG0WUYpqvVbWaLo7ee8p1kJsfVnIJ/30y2QRFxd1XRuiFWqCvGYeO6jIshZOkwe\n" +
            "twTvejjvJvJtcuXvWnq3HlF2zQqdyEJ9yiZxpFrTHGS38Z2S2gODQfbW7MTl2Jh2\n" +
            "sRyh0BP3Oi8M8izgixFWG2/X0lXepvVVuQENBGbEZnQBCADRsofO+2X30WPPpBtV\n" +
            "ZUou9azjwMkulTWtY3l0K2opiGSqRS+idvRVMOQMH9mm7bTjHXrHz/XXOG3d8cO6\n" +
            "3lAPIZcku+VOff/ise8a+fkMulek1GDWdcYEEQzlngyFlZ502b0vRCnHuL198mVW\n" +
            "JiDVxIKCvRYiRYS/obF77u8dktmTgfkS0X0K2QdWHgglX7C2rFc6k8Ofspn2ZHaL\n" +
            "X/qIz2742IK8BpdAM6HNRqlrbJoHVoNmVraZ1DEYZNMkPEbfOHggVOltEXWrVoxf\n" +
            "XAnQh0rfkg4WW3JyY1QFExhQ9iPhoBNm2vndT9b788/dAAuOc6IDh8AFr9wijLMF\n" +
            "LkVhABEBAAGJAR8EGAMCAAkFAmbEZnUCGwwACgkQ4i55eajsTmI4KAf/fT+lT32k\n" +
            "bVGRIrq7/YNK1aseZ2+gXC7SuY29/z36jm8M7QxkcqwVQea7qhnNGZb9ZiCohSgK\n" +
            "5/dz66TGcVPgSlBbQicAlacm9LaMElnK6T14Q6gjeukzHYHyeODQZxFVEFh1wDCP\n" +
            "IWgR4ByxyfSlUUJy0NksEMik1ytN/IfpbRAYOz5LMy9T/z35dWqq3GtFOmUO8Szj\n" +
            "JBlEvxL5cDIz/+lniPnjN3ZnMv1L6vFBuq61LA8EwpOGJl7h9RHpULk0kEsRCiFn\n" +
            "DaI2TstAMgzmdLOgSD3+JH4ADEoTrJ48l9TvDsOw8STjAWkCOpCqfKZM014MuXqC\n" +
            "yFV2wpYGIS79rQ==\n" +
            "=rD4N\n" +
            "-----END PGP PUBLIC KEY BLOCK-----";



    public static String otherPublicKey = "-----BEGIN PGP PUBLIC KEY BLOCK-----\n" +
            "\n" +
            "mQENBGbEQZgBCADD3n3VfnzpvlkE/ylL+Z3LIQRSL+dxq4cEX6xGgEpkEl460PC1\n" +
            "9BZDQV2pEYR0bseOkh4rNOfA5DSf3gIa+ceNb5J7iI0XDpxspccoXmm8vorALN1N\n" +
            "v+HYbhmqI/7tpw0rBnxwGblRXUgVMagV0u9vhlvQ+bPq/cPtgtr99OV4jLedMXzt\n" +
            "XdJiFBt/wIWDxYOnguPqJOgkw2PDj7Z+HR/QfZcSRvqog3PuWjaRtoyMdAD5263S\n" +
            "bF1LzedpxP0S7Fw0+e0mC/K57i9SyQPDWPIZAXaRLw49wwV8wTpDioJi9OV54k5F\n" +
            "FpKAUKgwBNRDNFTLF+I6kQUZiNXu24shXs77ABEBAAG0G0JyYWQgd29ya0kgPGJy\n" +
            "YWRAZ21haWwuY29tPokBVwQTAQgAQRYhBETE6zpCp44Es5itwMUfYEVa6AXtBQJm\n" +
            "xEGYAhsDBQkFo22oBQsJCAcCAiICBhUKCQgLAgQWAgMBAh4HAheAAAoJEMUfYEVa\n" +
            "6AXtufAH/3eSDRp8nIqQZPA+AUxYYFViKobKKGcTC4++UmvYswyfo6nQj/km43lX\n" +
            "pVAoVBfO/+bY5SjAwEWbvMb2HkHs+ItoEGEccbAUVOLwro2KdPWj7sljsAHp08JD\n" +
            "0VF5DGADuH3i5R5BebEeBSBzVbFWAGaTy/seaUPL4zHlBFMQYkvtb4TIFQ2eGCQp\n" +
            "KQg0m0p9F5op2nyMJhzoc/sCq2/pnHEeIgAUK5lOZl0wBzI8B5iByfaBxn5+KMfM\n" +
            "G21rc9AWOMF/LLiWlsYPNTfqbq88FnXAMNmUTb1JqxU5DcZVyUYSYXOlLAM0kwoW\n" +
            "AekwNe7ZusZOhl3YDG0r/nknjJePKLC5AQ0EZsRBmAEIANH/qPtm/p7YfYgWP1DF\n" +
            "WXAOU9RYXKMOENtAanjfD8j2n6udm7MovC6xjx7D70/ejxIAgkY+My0el8/g1Xfd\n" +
            "IFBpdhm/oduSLyWNqa4stbPe8lrihsdQ4wDG94rs83tX34jWtS5pHd7iOmbq+c9R\n" +
            "IfKw84fnJmU19jww+VDxp8d+8RqzsF/Lha2Xf4PWpLYZ7zDAwtS6KKF+Fo3h2ceb\n" +
            "4Zas0G9jS1nBpGg1i73Sryx2cKw/jzbxjIgbIadh/2OPFvKApiVyuBwsRpazuaRJ\n" +
            "bp3i2Gk2GvXTUELz5UDM/PXYZbf07he8V2iWYA0stDfD7qHpZ7/RmSy2y6iltWWV\n" +
            "MdUAEQEAAYkBPAQYAQgAJhYhBETE6zpCp44Es5itwMUfYEVa6AXtBQJmxEGYAhsM\n" +
            "BQkFo22oAAoJEMUfYEVa6AXtdKYH/iuqYT9SsmnlEIGWTqV9FEYNnxgQHu5FgZQz\n" +
            "LxYCHleesQVEwgvhMaWOdSXOFFcsCLuuLcsgSk6h9DXP0X8vG4rBoUKqnIzsAMdX\n" +
            "N6orMteYlxvFyacXLih8SRMsdqw3GgeDb3iAQYo7O3tQzGzC/glEXGote2eGR8TS\n" +
            "HEBsFmmcyDO7SowQUr3uvy6N3cvxM5sgOhQDI9YTLbX0bwcHxQBbs3+limmHyuvu\n" +
            "YUsC8mMfUq29+HA6rX/eoFIIMoQQYL1QsBeXawkAmeiJ8iASerlkNZTa7v+uc81P\n" +
            "a4h6aM6NbbOv24YmR4ydmCbUn3nhHO1a171RbNi82yUa/6IR9EI=\n" +
            "=c/RI\n" +
            "-----END PGP PUBLIC KEY BLOCK-----\n";

    public static void main(String[] args) throws Exception {
        testGPG4WinPublicKey();
//        testDecryptMessage();
    }

    public static void testGPG4WinPublicKey()throws Exception {
        String name ="Brad Yan", email="changeToemailPin@test.com", password ="12345678";
        int rsaKeySize=2048;

        String unEncryptMessage = "this is a test for GPG@@@;";

        String encryptMessage = PGPEncryptDecryptUtil.encryptAndSign(unEncryptMessage,email, password,KeyRings.ArmoredKeyPair.of(armorPrivateKey,armorPublicKey),"brad@gmail.com", otherPublicKey);
        System.out.println(encryptMessage);
    }

    public static void testDecryptMessage()throws Exception {
        String encryptMessage = "-----BEGIN PGP MESSAGE-----\n" +
                "\n" +
                "hQEMAwoX34w0WHGuAQf6A90WN+dbJPxsR3tGlxiNgYGw6S3AbqN8gqh5U6m3pmsw\n" +
                "i6hkAfDq4x8MNx8dxTiAt9LrESAz+PivIY6DPLGDUTVNl+LBi00EWS1eSnsTD5Ii\n" +
                "1OEIaU1KO9RrlF1OgTRHXl5mmbawJx7k8CybTCytBaDSrFzU37nhU2HE4TgJ4/PC\n" +
                "guaMzDQRPF2qJDofTYYZB3dIRud22t57zm1OfBGhn5LEu/QgyeCyki6jtu8n1dtj\n" +
                "0Q4cHmdfwfybbhI3GZS/8E7dxwkZJywR2Iy2oAO1shrbtllFgdznBm8OryskaqPe\n" +
                "eK2TIsconSDH48XqFiG25tr+S5kkgUEr4avFn7xbltLA3AFMSgtxcx0V4AZrhtAk\n" +
                "qH8/hR3yGeeYsBqCJb4HGktnlfPs617GY6VNrEn3bqE8NUSYB04cvNKsMlgH/etW\n" +
                "PxpkKOkDg8ePShXbSF61/yRmsUSM/lSUFz96JTRjItZfbQ4NSrPm27M8AdOj9qX0\n" +
                "vBHXoE7RVHuc8973DNNVqjtfiEEYwtUVwGKrjP6HOzrCL3GesfnzQYgVJH85UVo1\n" +
                "SkrSz7JtKLbmpM2jTMnsXxwfmqPToMwl1gltLI1QhFa/0If31VL0zD0bNSpdjeyR\n" +
                "tFtxtxclM9A5Ezp/xODF5Num2aCMDQkrK6pEdRBQNnUr2EaSJzw0wWMUy78zkhgG\n" +
                "hJrcgm7hRoNHYfKQ9feu8YC4hitwzIWvo90/UbqiLZ/6R0DBNsn7+m2RUZRS/gfP\n" +
                "ExiHRYhquV4Em5gfwxwHYonhuv5vnpz/M9AUvKa02lVEwSsTl6sbIcQetCrA0a2b\n" +
                "/B9E8slHHl+3GElt1IeF4ueqEcqwtqranuQfKeHviQqyqE1HfEX42F/UIxp1vaFH\n" +
                "aUgKK4PjztMEYXXIiCc=\n" +
                "=lJAb\n" +
                "-----END PGP MESSAGE-----";


        String name ="Brad Yan", email="changeToemailPin@test.com", password ="12345678";
        String decryptMessage = PGPEncryptDecryptUtil.decryptAndVerify(encryptMessage, password, KeyRings.ArmoredKeyPair.of(armorPrivateKey,armorPublicKey), "brad@gmail.com", otherPublicKey);

        System.out.println(decryptMessage);
    }
}
