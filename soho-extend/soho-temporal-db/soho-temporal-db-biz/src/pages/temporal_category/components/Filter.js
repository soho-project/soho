import React, { Component } from 'react'
import PropTypes from 'prop-types'
import moment from 'moment'
import { FilterItem } from 'components'
import { Trans,t } from "@lingui/macro"
import { Button, Row, Col, DatePicker, Form, Input, Cascader, Select } from 'antd'
import FilterForm from '../../../components/FilterForm'
const { Search } = Input
const { RangePicker } = DatePicker

class Filter extends Component {
  formRef = React.createRef()

  handleFields = fields => {
    const { betweenCreatedTime } = fields
    if (betweenCreatedTime && betweenCreatedTime.length) {
      fields.betweenCreatedTime = [
        moment(betweenCreatedTime[0]).format('YYYY-MM-DD'),
        moment(betweenCreatedTime[1]).format('YYYY-MM-DD'),
      ]
    }
    return fields
  }

  handleSubmit = () => {
    const { onFilterChange } = this.props
    const values = this.formRef.current.getFieldsValue()
    const fields = this.handleFields(values)
    onFilterChange(fields)
  }

  render() {
    const { onAdd, filter, options, trees } = this.props

    let initialCreateTime = []
    if (filter['betweenCreatedTime[0]']) {
      initialCreateTime[0] = moment(filter['betweenCreatedTime[0]'])
    }
    if (filter['betweenCreatedTime[1]']) {
      initialCreateTime[1] = moment(filter['betweenCreatedTime[1]'])
    }

    return (
      <FilterForm onAdd={onAdd} onSubmit={this.handleSubmit} ref={this.formRef} initialValues={{ ...filter, betweenCreatedTime: initialCreateTime }}>
            <Form.Item name="name">
              <Search
                placeholder={t`Name`}
                onSearch={this.handleSubmit}
              />
            </Form.Item>
            <Form.Item name="title">
              <Search
                placeholder={t`Title`}
                onSearch={this.handleSubmit}
              />
            </Form.Item>
            <FilterItem label={t`Created Time`}>
              <Form.Item name="betweenCreatedTime">
                <RangePicker
                  style={{ width: '100%' }}
                />
              </Form.Item>
            </FilterItem>
      </FilterForm>
    )
  }
}

Filter.propTypes = {
  onAdd: PropTypes.func,
  filter: PropTypes.object,
  onFilterChange: PropTypes.func,
}

export default Filter
