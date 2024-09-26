<template>
  <div>
    <h2 id="page-heading" data-cy="MstJobHeading">
      <span v-text="t$('monolithApp.mstJob.home.title')" id="mst-job-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('monolithApp.mstJob.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'MstJobCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-mst-job"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('monolithApp.mstJob.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <div class="row">
      <div class="col-sm-12">
        <form name="searchForm" class="form-inline" v-on:submit.prevent="search(currentSearch)">
          <div class="input-group w-100 mt-3">
            <input
              type="text"
              class="form-control"
              name="currentSearch"
              id="currentSearch"
              v-bind:placeholder="t$('monolithApp.mstJob.home.search')"
              v-model="currentSearch"
            />
            <button type="button" id="launch-search" class="btn btn-primary" v-on:click="search(currentSearch)">
              <font-awesome-icon icon="search"></font-awesome-icon>
            </button>
            <button type="button" id="clear-search" class="btn btn-secondary" v-on:click="clear()" v-if="currentSearch">
              <font-awesome-icon icon="trash"></font-awesome-icon>
            </button>
          </div>
        </form>
      </div>
    </div>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && mstJobs && mstJobs.length === 0">
      <span v-text="t$('monolithApp.mstJob.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="mstJobs && mstJobs.length > 0">
      <table class="table table-striped" aria-describedby="mstJobs">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('jobTitle')">
              <span v-text="t$('monolithApp.mstJob.jobTitle')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'jobTitle'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('minSalary')">
              <span v-text="t$('monolithApp.mstJob.minSalary')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'minSalary'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('maxSalary')">
              <span v-text="t$('monolithApp.mstJob.maxSalary')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'maxSalary'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('employee.id')">
              <span v-text="t$('monolithApp.mstJob.employee')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'employee.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="mstJob in mstJobs" :key="mstJob.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'MstJobView', params: { mstJobId: mstJob.id } }">{{ mstJob.id }}</router-link>
            </td>
            <td>{{ mstJob.jobTitle }}</td>
            <td>{{ mstJob.minSalary }}</td>
            <td>{{ mstJob.maxSalary }}</td>
            <td>
              <div v-if="mstJob.employee">
                <router-link :to="{ name: 'MstEmployeeView', params: { mstEmployeeId: mstJob.employee.id } }">{{
                  mstJob.employee.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'MstJobView', params: { mstJobId: mstJob.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'MstJobEdit', params: { mstJobId: mstJob.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(mstJob)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span id="monolithApp.mstJob.delete.question" data-cy="mstJobDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-mstJob-heading" v-text="t$('monolithApp.mstJob.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-mstJob"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeMstJob()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="mstJobs && mstJobs.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./mst-job.component.ts"></script>
