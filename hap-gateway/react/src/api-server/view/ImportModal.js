import React from 'react';
import { Form, TextField } from 'choerodon-ui/pro';
import { $l } from '@choerodon/boot';
import '../index.scss';

export default ({ onChange }) => {
  function handleChange(value, oldValue) {
    onChange(value);
  }

  return (
    <div>
      <Form>
        <TextField
          onChange={handleChange}
          name="importUrl"
          class="gateway-input-import-url"
          required
          placeholder={$l('server.importurl.description')}
        />
      </Form>
    </div>
  );
};
