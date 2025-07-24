package provider;

import ir.moke.microfox.api.mybatis.MyBatisUtils;
import model.Address;
import model.Client;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class AddressProvider {
    public String insert(Map<String, Object> params) {
        Address address = (Address) params.get("address");
        Client client = (Client) params.get("client");
        return new SQL()
                .INSERT_INTO("address")
                .VALUES(MyBatisUtils.getColumns(Address.class) + ",client_id", MyBatisUtils.getValues(address) + "," + client.getId())
                .toString();
    }

    public String selectAll() {
        return new SQL()
                .SELECT("*")
                .FROM("address")
                .toString();
    }

    public String selectById(long id) {
        return new SQL()
                .SELECT(MyBatisUtils.getColumns(Address.class))
                .FROM("address")
                .WHERE("id=#{id}")
                .toString();
    }
}
