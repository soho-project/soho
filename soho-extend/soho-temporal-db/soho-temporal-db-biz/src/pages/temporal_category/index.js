import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import {connect, history} from 'umi'
import { stringify } from 'qs'
import { t } from "@lingui/macro"
import { Page } from 'components'
import List from './components/List'
import TemporalCategoryModal from "./components/Modal";
import Filter from "./components/Filter"

import SohoTree from "./components/SohoTree";

@connect(({ temporal_category, loading }) => ({ temporal_category, loading }))
class TemporalCategory extends PureComponent {
  handleRefresh = newQuery => {
    const { location } = this.props
    const { query, pathname } = location
    if(newQuery && newQuery.createTime) {
      newQuery.startDate = newQuery.createTime[0]
      newQuery.endDate = newQuery.createTime[1]
      delete newQuery.createTime
    }
    history.push({
      pathname,
      search: stringify(
        {
          ...query,
          ...newQuery,
        },
        { arrayFormat: 'repeat' }
      ),
    })
  }
  handleTabClick = key => {
    const { pathname } = this.props.location

    history.push({
      pathname,
      search: stringify({
        status: key,
      }),
    })
  }

  get listProps() {
    const { temporal_category, loading, location, dispatch } = this.props
    const { list, pagination } = temporal_category
    const { query, pathname } = location
    return {
      options: temporal_category.options,
      trees: temporal_category.trees,
      optionValueLabel: temporal_category.optionValueLabel,
      pagination,
      dataSource: list,
      loading: loading.effects['temporal_category/query'],
      onChange(page) {
        history.push({
          pathname,
          search: stringify({
            ...query,
            page: page.current,
            pageSize: page.pageSize,
          }),
        })
      },
      onDeleteItem: id => {
        dispatch({
          type: 'temporal_category/delete',
          payload: id,
        }).then(() => {
          this.handleRefresh({
            page:
              list.length === 1 && pagination.current > 1
                ? pagination.current - 1
                : pagination.current,
          })
        })
      },
      onEditItem(item) {
        dispatch({
          type: 'temporal_category/showModal',
          payload: {
            modalType: 'update',
            currentItem: item,
          },
        })
      },
      onApplyItem(item) {
        dispatch({
          type: 'temporal_category/showModal',
          payload: {
            modalType: 'apply',
            currentItem: item,
          }
        })
      },
      onChildItem(item) {
        dispatch({
          type: 'temporal_category/showModal',
          payload: {
            modalType: 'create',
            currentItem: {...item},
          },
        })
      },    }
  }

  get modalProps() {
    const { dispatch, temporal_category, loading } = this.props
    const { currentItem, modalVisible, modalType,resourceTree,resourceIds } = temporal_category

    return {
      options: temporal_category.options,
      trees: temporal_category.trees,
      optionValueLabel: temporal_category.optionValueLabel,
      item: modalType === 'create' ? {...currentItem} : currentItem,
      visible: modalVisible,
      destroyOnClose: true,
      maskClosable: false,
      confirmLoading: loading.effects[`temporal_category/${modalType}`],
      resourceTree: resourceTree,
      resourceIds: resourceIds,
      title: `${
          modalType === 'create' ? t`Create` : (modalType === 'update' ? t`Update` : t`Apply`)
      }`,
      centered: true,
      onOk: data => {
        dispatch({
          type: `temporal_category/${modalType}`,
          payload: data,
        }).then(() => {
          this.handleRefresh()
        })
      },
      onCancel() {
        dispatch({
          type: 'temporal_category/hideModal',
        })
      },
    }
  }

  get filterProps() {
    const { location, dispatch, temporal_category} = this.props
    const {query,pathname} = location
    return {
      options: temporal_category.options,
      trees: temporal_category.trees,
      optionValueLabel: temporal_category.optionValueLabel,
      filter: {
        ...query,
      },
      onFilterChange(values) {
        history.push({
          pathname,
          search: stringify(values),
        })
      },
      onAdd() {
        dispatch({
          type: 'temporal_category/showModal',
          payload: {
            modalType: 'create',
            currentItem: {},
          },
        })
      },
    }
  }

  render() {
    return (
      <Page inner>

<SohoTree {...this.listProps} />
        <TemporalCategoryModal {...this.modalProps} />
      </Page>
    )
  }
}

TemporalCategory.propTypes = {
  temporal_category: PropTypes.object,
  loading: PropTypes.object,
  location: PropTypes.object,
  dispatch: PropTypes.func,
}

export default TemporalCategory
