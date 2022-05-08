import com.google.common.collect.Lists;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.hooks.*;
import org.apache.commons.collections4.map.LazySortedMap;
import org.slf4j.*;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Luncher {

    static Map<String, Object> map = new LinkedHashMap<>();

    public static void main(String[] args) {
        Logger LOGGER = LoggerFactory.getLogger(Luncher.class);

        map.put("KEY1", 0);
        map.put("KEY2", 1);
        map.put("KEY3", 2);
        map.put("KEY4", 3);
        map.put("KEY5", 0);
        map.put("KEY6", 0);
        map.put("KEY7", 0);
        map.put("KEY8", 0);
        map.put("KEY9", 0);

        List<Map<String, Object>> list = new LinkedList<>();
//        System.out.println(Lists.partition(list, 3).stream());
    }

}
