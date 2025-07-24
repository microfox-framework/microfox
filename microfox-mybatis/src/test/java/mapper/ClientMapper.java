package mapper;

import model.Client;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ClientMapper {

    @Insert("insert into client (id,name,family) values (#{id},#{name},#{family})")
    @SelectKey(statement = "SELECT client_seq.NEXTVAL FROM dual", keyProperty = "id", before = true, resultType = Long.class)
    void save(Client client);

    @Select("select * from client")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "name", property = "name"),
            @Result(column = "family", property = "family"),
            @Result(property = "addresses", column = "id",
                    many = @Many(select = "ir.moke.example.persistence.mapper.AddressMapper.findAddressByClientId")),
    })
    List<Client> findAll();

    @Select("select * from client where id=#{id}")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "name", property = "name"),
            @Result(column = "family", property = "family"),
            @Result(property = "addresses", column = "id",
                    many = @Many(select = "ir.moke.example.persistence.mapper.AddressMapper.findAddressByClientId")),
    })
    Client findById(long id);
}
