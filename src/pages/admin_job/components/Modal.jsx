import React, { useRef,useState,useEffect } from 'react'
import {Form, Input, InputNumber, Radio, Modal, Select, message,Checkbox,DatePicker,TreeSelect} from 'antd'
import TextArea from "antd/es/input/TextArea";
import {useLingui, Trans} from "@lingui/react/macro"
import ImgCrop from "antd-img-crop";
import {LoadingOutlined, PlusOutlined} from "@ant-design/icons";
import moment from 'moment';
import apis from '@/apis'
import store from "store";
import QuillEditor from "@/component/form/editor";
import CodeEditor from "@/component/form/codeEditor"
import JsonEditor from "@/component/form/jsonEditor"
import UploadWithInput from '@/component/form/upload/UploadWithInput';
import {addRootNode} from "@/utils/tree";
import Upload from '@/component/form/upload/UploadFile'
import dayjs from 'dayjs';

const {createUserAvatar, createAdminContentImage} = apis

const { Option } = Select;
const FormItem = Form.Item
//时间格式化
const datetimeFormat = "YYYY-MM-DD HH:mm:ss"
const dateFormat = "YYYY-MM-DD"
const formItemLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 14,
  },
}

export default (props) => {
  const formRef = useRef()
  const { i18n, t } = useLingui()

  const [state,setState] = useState({
    value: [],
    loading: false, //上传 loading
  });
  const { baseState={}, item = {}, onOk, form, ...modalProps } = props
  const {options={}, optionValueLabel={}, trees={}} = baseState;

  const handleOk = () => {
    const { item = {}, onOk } = props
    formRef.current.validateFields()
      .then(values => {
        const data = {
          ...values,
          id: item.id,          //时间值处理
        }
        onOk(data)
      })
      .catch(errorInfo => {
        console.log(errorInfo)
      })
  }


  const initData = (item) => {
    return {
      ...item,
    
    }
  }

  useEffect(() => {
    if (props.open) {
      let data = initData(item)
      if(item?.id) {
        formRef.current.setFieldsValue(data)
      } else {
        formRef.current.resetFields()
        formRef.current.setFieldsValue(data)
      }
    }
  }, [item])
  
  
    return (
      <Modal {...modalProps} onOk={handleOk} width={1300}>
        <Form ref={formRef} name="control-ref" layout="horizontal">
         <FormItem  name='name' rules={[{ required: true }]} label={t("Name")} hasFeedback {...formItemLayout}>
            <Input disabled={false} maxLength={1000} />
          </FormItem>
         <FormItem  name='canConcurrency' rules={[{ required: false }]} label={t("Can Concurrency")} hasFeedback {...formItemLayout}>
            <InputNumber  maxLength={11} step="1"  style={{width:200}} disabled={false}  />
          </FormItem>
         <FormItem  name='cmd' rules={[{ required: true }]} label={t("Cmd")} hasFeedback {...formItemLayout}>
            <Input disabled={false} maxLength={10000} />
          </FormItem>
         <FormItem  name='status' rules={[{ required: true }]} label={t("Status")} hasFeedback {...formItemLayout}>
           <Select
             showSearch
             placeholder={t`Status`}
             optionFilterProp="children"
             options={optionValueLabel?.status}
             disabled={false}
             />
          </FormItem>
         <FormItem  name='cron' rules={[{ required: true }]} tooltip="格式(0 0/5 * * * ?)"  label={t("Cron")} hasFeedback {...formItemLayout}>
            <Input disabled={false} maxLength={100} />
          </FormItem>
        </Form>
      </Modal>
    )
}