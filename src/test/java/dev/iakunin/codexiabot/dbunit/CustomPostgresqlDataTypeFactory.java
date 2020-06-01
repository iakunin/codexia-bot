package dev.iakunin.codexiabot.dbunit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Optional;
import org.dbunit.dataset.datatype.AbstractDataType;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.postgresql.util.PGobject;

// @todo #6 this DataTypeFactory should be pull-requested to dbunit
//  for more info see
//  https://github.com/database-rider/database-rider/issues/102#issuecomment-511241871
public final class CustomPostgresqlDataTypeFactory extends PostgresqlDataTypeFactory {

    private static final String TYPE_NAME = "json";

    @Override
    public DataType createDataType(final int type, final String name) throws DataTypeException {
        return TYPE_NAME.equals(name)
            ? new JsonDataType(TYPE_NAME)
            : super.createDataType(type, name);
    }

    public static final class JsonDataType extends AbstractDataType {

        private final String name;

        public JsonDataType(final String name) {
            super(name, Types.OTHER, String.class, false);
            this.name = name;
        }

        @Override
        public Object typeCast(final Object obj) {
            return obj.toString();
        }

        @Override
        public Object getSqlValue(
            final int column,
            final ResultSet resultset
        ) throws SQLException {
            return resultset.getString(column);
        }

        @Override
        public void setSqlValue(
            final Object value,
            final int column,
            final PreparedStatement statement
        ) throws SQLException {
            final PGobject json = new PGobject();
            json.setType(this.name);
            json.setValue(
                Optional.ofNullable(value)
                    .map(Object::toString)
                    .orElse(null)
            );
            statement.setObject(column, json);
        }
    }
}
