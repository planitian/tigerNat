import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.plani.work.common.ParentInitializer;
import com.plani.work.common.ServerHelper;
import com.plani.work.common.component.PlaniChannelGroup;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.DefaultChannelId;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class localTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {


        Gson gson = new Gson();
        String jsonStr = "{\n" +
                "\t\"5144790450569216\": {\n" +
                "\t\t\"兖州煤业股份有限公司\": [\"裁判文书\"],\n" +
                "\t\t\"中国机械进出口（集团）有限公司\": [\"裁判文书\"],\n" +
                "\t\t\"阿里巴巴（中国）网络技术有限公司\": [\"开庭公告\", \"开庭公告\"],\n" +
                "\t\t\"华为技术有限公司\": [\"开庭公告\"],\n" +
                "\t\t\"苏州联胜化学有限公司\": [\"裁判文书\"],\n" +
                "\t\t\"中国农业银行股份有限公司\": [\"开庭公告\"]\n" +
                "\t},\n" +
                "\t\"6801931301355520\": {\n" +
                "\t\t\"兖州煤业股份有限公司\": [\"裁判文书\"],\n" +
                "\t\t\"中国机械进出口（集团）有限公司\": [\"裁判文书\"],\n" +
                "\t\t\"阿里巴巴（中国）网络技术有限公司\": [\"开庭公告\", \"开庭公告\"],\n" +
                "\t\t\"华为技术有限公司\": [\"开庭公告\"],\n" +
                "\t\t\"苏州联胜化学有限公司\": [\"裁判文书\"],\n" +
                "\t\t\"中国农业银行股份有限公司\": [\"开庭公告\"]\n" +
                "\t}\n" +
                "}";
        JsonObject jsonObject = gson.fromJson(jsonStr, JsonObject.class);
        //如果是  jsonObject
        if (jsonObject.isJsonObject()) {
            JsonObject org = jsonObject.getAsJsonObject("5144790450569216");
            System.out.println(org);

            JsonArray dongs = org.getAsJsonArray("兖州煤业股份有限公司");
            dongs.forEach(new Consumer<JsonElement>() {
                @Override
                public void accept(JsonElement jsonElement) {
                    System.out.println(jsonElement);
                }
            });

        }
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        String toJson = gson.toJson(map);
        System.out.println(toJson);
    }
}
