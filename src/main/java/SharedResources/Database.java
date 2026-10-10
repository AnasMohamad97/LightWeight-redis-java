package SharedResources;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
    private final Map<String, CacheKey> strings = new ConcurrentHashMap<>();
    private final Map<String , List<String>> lists = new HashMap<>();
    private final ConcurrentHashMap<String,HashMap<String, HashMap<String, String>>> stream = new ConcurrentHashMap<>();
    private final Object lock = new Object();

    public void setStream(String Streamkey,String entry , String key , String value ) {
        synchronized (lock) {
            if(!stream.containsKey(Streamkey)) {
                stream.put(Streamkey, new HashMap<>());
                stream.get(Streamkey).put(entry,new HashMap<>());
                stream.get(Streamkey).get(entry).put(key,value);
            }else {
                stream.get(Streamkey).get(entry).put(key,value);
            }
        }
    }
    public String get(String key) {
        synchronized (lock) {
            return strings.get(key).getValue();
        }
    }
    public void set(String key , String value  , Long expiresAtMillis){
        synchronized (lock) {
            strings.put(key, new CacheKey(key, value, expiresAtMillis));
        }
    }
    public  int rpush(String key , List<String>values){
        synchronized (lock) {
            List<String> list = lists.computeIfAbsent(key, k -> new ArrayList<>());
            list.addAll(values);
            lock.notifyAll(); // as they should notify BLPOP that this has been triggered
            return list.size();
        }
    }
    public  int lpush(String key , List<String>values){
        synchronized (lock) {
            List<String> list = lists.computeIfAbsent(key, k -> new ArrayList<>());
            for (String value : values) {
                list.addFirst(value);
            }
            lock.notifyAll(); // as they should notify BLPOP that this has been triggered
            return list.size();
        }
    }
    public  int llen(String key){
        synchronized (lock) {
            List<String> list = lists.get(key);
            return list == null ? 0 : list.size();
        }
    }
    public  List<String> lpop(String key, int count) {
        synchronized (lock) {
            List<String> list = lists.get(key);
            if (list == null) {
                return null;
            }
            List<String> removed = new ArrayList<>();
            while (removed.size() < count && !list.isEmpty()) {
                removed.add(list.removeFirst());
            }
            if (removed.isEmpty()) lists.remove(key);
            return removed;
        }
    }
    public  List<String> lrange(String key , int start, int end) {
        synchronized (lock) {
            List<String> list = lists.get(key);
            if (list == null) {
                return List.of();
            }
            int size = list.size();
            start = Math.max(normalize(start, size), 0);
            end = normalize(end, size);
            if (start >= size || start > end) return List.of();
            end = Math.min(end + 1, size); // inclusivity of the ending index

            return new ArrayList<>(list.subList(start, end));
        }

    }

    private int normalize(int index, int size) {
        return index < 0 ? index + size : index;
    }
    public  List<String> blpop(String key , long timeoutMillis) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        synchronized (lock) {
            List<String> list;
            while ((list = lists.get(key)) == null || list.isEmpty()) {
                if (timeoutMillis == 0) { lock.wait(); continue; }
                long remaining = deadline - System.currentTimeMillis();
                if (remaining <= 0) return List.of();
                lock.wait(remaining);
            }
            return  List.of(key,list.removeFirst());
        }
    }
    public String getType(String key){
        synchronized (lock) {
            if(strings.containsKey(key))return "string";
            else if(lists.containsKey(key))return "list";
            else if(stream.containsKey(key))return "stream";
            else return "none";
        }
    }

}
