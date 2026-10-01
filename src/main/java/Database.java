import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
    private final Map<String,CacheKey> strings = new ConcurrentHashMap<>();

    private final Map<String , List<String>> lists = new HashMap<>();

    public String get(String key) {
        return strings.get(key).getValue();

    }
    public void set(String key , String value  , Long expiresAtMillis){
        strings.put(key, new CacheKey(key,value,expiresAtMillis));
    }
    public synchronized int rpush(String key , List<String>values){
        List<String> list = lists.computeIfAbsent(key,k-> new ArrayList<>());
        list.addAll(values);
        notifyAll(); // as they should notify BLPOP that this has been triggered
        return list.size();
    }
    public synchronized int lpush(String key , List<String>values){
        List<String> list = lists.computeIfAbsent(key,k-> new ArrayList<>());
        for(String value : values){
            list.addFirst(value);
        }
        notifyAll(); // as they should notify BLPOP that this has been triggered
        return list.size();
    }
    public synchronized int llen(String key){
        List<String> list = lists.get(key);
        return list == null ?0:list.size();
    }
    public synchronized List<String> lpop(String key, int count) {
        List<String>list=lists.get(key);
        if(list==null){
            return null;
        }
        List<String> removed = new ArrayList<>();
        while(removed.size()<count&&!list.isEmpty()){
            removed.add(list.removeFirst());
        }
        if(removed.isEmpty())lists.remove(key);
        return removed;
    }
    public synchronized List<String> lrange(String key , int start, int end) {
        List<String>list=lists.get(key);
        if(list==null){
            return List.of();
        }
        int size = list.size();
        start = Math.max(normalize(start,size),0);
        end = normalize(end,size);
        if(start >= size || start > end) return List.of();
        end  = Math.min(end + 1 , size); // inclusivity of the ending index

        return new ArrayList<>(list.subList(start,end));

    }

    private int normalize(int index, int size) {
        return index < 0 ? index + size : index;
    }
    public synchronized List<String> blpop(String key , long timeoutMillis) throws InterruptedException {
        lists.computeIfAbsent(key,k-> new ArrayList<>());

        if(!lists.get(key).isEmpty()){
           return lpop(key,1);
        }
        if(timeoutMillis == 0) {
            while (lists.get(key).isEmpty()) {
                wait();
            }
            List<String> removed = lpop(key, 1);
            removed.addFirst(key);
            return removed;
        }else {
          wait(timeoutMillis);
        }
        return lists.get(key).isEmpty() ? List.of() : lpop(key,1);
    }

}
