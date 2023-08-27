import React, { Component } from 'react'
import PropTypes from 'prop-types'
import moment from 'moment'
import { FilterItem } from 'components'
import { Trans,t } from "@lingui/macro"
import { Button, Row, Col, DatePicker, Form, Input, Cascader, Select } from 'antd'

const { Search } = Input
const { RangePicker } = DatePicker
const ColProps = {
  xs: 24,
  sm: 12,
  style: {
    marginBottom: 16,
  },
}

const TwoColProps = {
  ...ColProps,
  xl: 96,
}

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

  handleReset = () => {
    const fields = this.formRef.current.getFieldsValue()
    for (let item in fields) {
      if ({}.hasOwnProperty.call(fields, item)) {
        if (fields[item] instanceof Array) {
          fields[item] = []
        } else {
          fields[item] = undefined
        }
      }
    }
    this.formRef.current.setFieldsValue(fields)
    this.handleSubmit()
  }
  handleChange = (key, values) => {
    const { onFilterChange } = this.props
    let fields = this.formRef.current.getFieldsValue()
    fields[key] = values
    fields = this.handleFields(fields)
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
      <Form ref={this.formRef} name="control-ref" initialValues={{ ...filter, betweenCreatedTime: initialCreateTime }}>
        <Row gutter={24}>
          <Col
            {...ColProps}
            xl={{ span: 6 }}
            md={{ span: 8 }}
            sm={{ span: 12 }}
            id="createTimeRangePicker"
          >
            <FilterItem label={t`Created Time`}>
              <Form.Item name="betweenCreatedTime">
                <RangePicker
                  style={{ width: '100%' }}
                />
              </Form.Item>
            </FilterItem>
          </Col>
          <Col
            {...TwoColProps}
            xl={{ span: 10 }}
            md={{ span: 24 }}
            sm={{ span: 24 }}
          >
            <Row type="flex" align="middle" justify="space-between">
              <div>
                <Button
                  type="primary" htmlType="submit"
                  className="margin-right"
                  onClick={this.handleSubmit}
                >
                  <Trans>Search</Trans>
                </Button>
                <Button onClick={this.handleReset}>
                  <Trans>Reset</Trans>
                </Button>
              </div>
              <Button type="ghost" onClick={onAdd}>
                <Trans>Create</Trans>
              </Button>
            </Row>
          </Col>
        </Row>
      </Form>
    )
  }
}

Filter.propTypes = {
  onAdd: PropTypes.func,
  filter: PropTypes.object,
  onFilterChange: PropTypes.func,
}

export default Filter
