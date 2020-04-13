package dev.iakunin.codexiabot.dbunit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.dbunit.dataset.datatype.AbstractDataType;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.postgresql.util.PGobject;

// @todo #6 this DataTypeFactory should be pull-requested to dbunit
//  for more info see https://github.com/database-rider/database-rider/issues/102#issuecomment-511241871
public class CustomPostgresqlDataTypeFactory extends PostgresqlDataTypeFactory {
    @Override
    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
        if (sqlTypeName.equals("json")) {
            return new JsonDataType();
        } else {
            return super.createDataType(sqlType, sqlTypeName);
        }
    }

    public static class JsonDataType extends AbstractDataType {

        public JsonDataType() {
            super("json", Types.OTHER, String.class, false);
        }

        @Override
        public Object typeCast(Object obj) {
            return obj.toString();
        }

        @Override
        public Object getSqlValue(int column, ResultSet resultSet) throws SQLException {
            return resultSet.getString(column);
        }

        @Override
        public void setSqlValue(Object value,
                                int column,
                                PreparedStatement statement) throws SQLException {
            final PGobject jsonObj = new PGobject();
            jsonObj.setType("json");
            jsonObj.setValue(value == null ? null : value.toString());

            statement.setObject(column, jsonObj);
        }
    }
}
