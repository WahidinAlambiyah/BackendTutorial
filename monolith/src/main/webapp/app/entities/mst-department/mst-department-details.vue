<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="mstDepartment">
        <h2 class="jh-entity-heading" data-cy="mstDepartmentDetailsHeading">
          <span v-text="t$('monolithApp.mstDepartment.detail.title')"></span> {{ mstDepartment.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.mstDepartment.departmentName')"></span>
          </dt>
          <dd>
            <span>{{ mstDepartment.departmentName }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstDepartment.location')"></span>
          </dt>
          <dd>
            <div v-if="mstDepartment.location">
              <router-link :to="{ name: 'LocationView', params: { locationId: mstDepartment.location.id } }">{{
                mstDepartment.location.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="mstDepartment.id"
          :to="{ name: 'MstDepartmentEdit', params: { mstDepartmentId: mstDepartment.id } }"
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

<script lang="ts" src="./mst-department-details.component.ts"></script>
