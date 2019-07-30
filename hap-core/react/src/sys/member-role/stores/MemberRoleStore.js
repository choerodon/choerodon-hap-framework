import { action, computed, observable } from 'mobx';
import { axios, store } from '@choerodon/boot';
import querystring from 'query-string';

class MemberRoleStore {
  @observable type = 'user';

  @observable userData = [];

  @observable userLoading = true;

  @observable userPagination = {};

  @observable userParams = {};

  @observable userSelections = [];

  @observable current = {};

  @observable roleData = [];

  @observable roleLoading = true;

  @observable rolePagination = {};

  @observable roleParams = {};

  @observable roleSelections = [];

  loadUserData = () => axios.post();

  loadRoleData = () => axios.post();

  loadUser = () => axios.get();

  updateRole = () => axios.post();

  deleteRole = () => axios.delete();
}
const memberRoleStore = new MemberRoleStore();
export default memberRoleStore;
