public class CachKey {

    public String key;
    public String value;
    public Long expireTime;

    public CachKey(String key,String value,Long expireTime) {
        this.key = key;
        this.value = value;
        this.expireTime = expireTime;
    }
    public CachKey(String key,String value) {
        this(key, value, null);
    }
    public String getKey() {
        return key;
    };
    public void setKey(String key) {
        this.key = key;
    }
    public String getValue() {
        if(expireTime == null)return value;
        else {
            if(expireTime > System.currentTimeMillis()) {
                return value;
            }else return null;
        }
    }
    public void setValue(String value) {
        this.value = value;
    }
    public Long getExpireTime() {
        return expireTime;
    }
    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }
}
