<template>
  <div>
    <!--页面区域-->
    <div class="page-view">
      <div class="table-operations">
        <a-space>
          <a-button @click="handleBatchDelete">批量删除</a-button>
        </a-space>
      </div>
      <a-table
        size="middle"
        rowKey="id"
        :loading="data.loading"
        :columns="columns"
        :data-source="data.list"
        :scroll="{ x: 'max-content' }"
        :row-selection="rowSelection"
        :pagination="{
          size: 'default',
          current: data.page,
          pageSize: data.pageSize,
          onChange: (current) => (data.page = current),
          showSizeChanger: false,
          showTotal: (total) => `共${total}条数据`,
        }"
      >
        <template #bodyCell="{ text, record, index, column }">
      
          <template v-if="column.key === 'operation'">
            <span>
              <a-divider type="vertical" />
              <a-popconfirm title="确定删除?" ok-text="是" cancel-text="否" @confirm="confirmDelete(record)">
                <a href="#">删除</a>
              </a-popconfirm>
            </span>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup>
  import { message } from 'ant-design-vue';
  import { RechargeRecordListApi } from '/@/api/user';
  import { BASE_URL } from '/@/store/constants';
  import * as utils from "../../utils";


  const columns = reactive([
    {
      title: '序号',
      dataIndex: 'index',
      key: 'index',
      align: 'center',
    },
    {
      title: '用户ID',
      dataIndex: 'userId',
      key: 'userId',
      align: 'center',
    },   
    {
      title: '充值金额',
      dataIndex: 'amount',
      key: 'amount',
      align: 'center',
    },
    {
      title: '充值时间',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      align: 'center',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      align: 'center',
    },

    {
      title: '操作',
      dataIndex: 'action',
      key: 'operation',
      align: 'center',
      fixed: 'right',
      width: 140,
    },
  ]);

  // 页面数据
  const data = reactive({
    list: [],
    loading: false,
    currentAdminUserName: '',
    keyword: '',
    selectedRowKeys: [],
    pageSize: 10,
    page: 1,
  });

  // 弹窗数据源
  const modal = reactive({
    visile: false,
    editFlag: false,
    title: '',
    form: {
      id: undefined,
      image: undefined,
      link: undefined,
    },
    rules: {
      link: [{ required: true, message: '请输入', trigger: 'change' }],
    },
  });

  onMounted(() => {
    getList();
  });

  const getList = () => {
    data.loading = true;
    RechargeRecordListApi({
      keyword: data.keyword,
    })
      .then((res) => {
        data.loading = false;
        console.log(res);
        res.data.forEach((item, index) => {
          item.index = index + 1;
          if (item.images) {
            //item.images = BASE_URL + item.images;
            item.images = `${BASE_URL}/api/staticfiles/dynamic/${item.images}`;
          }
          item.commentTime = utils.getFormatTime(item.commentTime, true);
        });
        data.list = res.data;
      })
      .catch((err) => {
        data.loading = false;
        console.log(err);
      });
  };
  // 处理动态图片路径
const getPostImgUrl = (postImg) => {
  return `${BASE_URL}/api/staticfiles/dynamic/${postImg}`;
};

  const rowSelection = ref({
    onChange: (selectedRowKeys, selectedRows) => {
      console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
      data.selectedRowKeys = selectedRowKeys;
    },
  });

  const handleAdd = () => {
  };

  const confirmDelete = (record) => {
    console.log('delete', record);
    deleteApi({ ids: record.id })
      .then((res) => {
        getList();
      })
      .catch((err) => {
        message.error(err.msg || '操作失败');
      });
  };

  const handleBatchDelete = () => {
    console.log(data.selectedRowKeys);
    if (data.selectedRowKeys.length <= 0) {
      console.log('hello');
      message.warn('请勾选删除项');
      return;
    }
    deleteApi({ ids: data.selectedRowKeys.join(',') })
      .then((res) => {
        message.success('删除成功');
        data.selectedRowKeys = [];
        getList();
      })
      .catch((err) => {
        message.error(err.msg || '操作失败');
      });
  };
</script>

<style scoped lang="less">
  .page-view {
    min-height: 100%;
    background: #fff;
    padding: 24px;
    display: flex;
    flex-direction: column;
  }

  .table-operations {
    margin-bottom: 16px;
    text-align: right;
  }

  .table-operations > button {
    margin-right: 8px;
  }
</style>
