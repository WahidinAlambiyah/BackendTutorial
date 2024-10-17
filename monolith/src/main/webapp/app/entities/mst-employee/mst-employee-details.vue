<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="mstEmployee">
        <h2 class="jh-entity-heading" data-cy="mstEmployeeDetailsHeading">
          <span v-text="t$('monolithApp.mstEmployee.detail.title')"></span> {{ mstEmployee.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.mstEmployee.firstName')"></span>
          </dt>
          <dd>
            <span>{{ mstEmployee.firstName }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstEmployee.lastName')"></span>
          </dt>
          <dd>
            <span>{{ mstEmployee.lastName }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstEmployee.email')"></span>
          </dt>
          <dd>
            <span>{{ mstEmployee.email }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstEmployee.phoneNumber')"></span>
          </dt>
          <dd>
            <span>{{ mstEmployee.phoneNumber }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstEmployee.hireDate')"></span>
          </dt>
          <dd>
            <span v-if="mstEmployee.hireDate">{{ formatDateLong(mstEmployee.hireDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstEmployee.salary')"></span>
          </dt>
          <dd>
            <span>{{ mstEmployee.salary }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstEmployee.commissionPct')"></span>
          </dt>
          <dd>
            <span>{{ mstEmployee.commissionPct }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstEmployee.manager')"></span>
          </dt>
          <dd>
            <div v-if="mstEmployee.manager">
              <router-link :to="{ name: 'MstEmployeeView', params: { mstEmployeeId: mstEmployee.manager.id } }">{{
                mstEmployee.manager.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstEmployee.department')"></span>
          </dt>
          <dd>
            <div v-if="mstEmployee.department">
              <router-link :to="{ name: 'MstDepartmentView', params: { mstDepartmentId: mstEmployee.department.id } }">{{
                mstEmployee.department.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstEmployee.mstDepartment')"></span>
          </dt>
          <dd>
            <div v-if="mstEmployee.mstDepartment">
              <router-link :to="{ name: 'MstDepartmentView', params: { mstDepartmentId: mstEmployee.mstDepartment.id } }">{{
                mstEmployee.mstDepartment.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="mstEmployee.id"
          :to="{ name: 'MstEmployeeEdit', params: { mstEmployeeId: mstEmployee.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./mst-employee-details.component.ts"></script>
