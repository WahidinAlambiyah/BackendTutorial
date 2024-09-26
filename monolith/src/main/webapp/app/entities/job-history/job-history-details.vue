<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="jobHistory">
        <h2 class="jh-entity-heading" data-cy="jobHistoryDetailsHeading">
          <span v-text="t$('monolithApp.jobHistory.detail.title')"></span> {{ jobHistory.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.jobHistory.startDate')"></span>
          </dt>
          <dd>
            <span v-if="jobHistory.startDate">{{ formatDateLong(jobHistory.startDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.jobHistory.endDate')"></span>
          </dt>
          <dd>
            <span v-if="jobHistory.endDate">{{ formatDateLong(jobHistory.endDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.jobHistory.language')"></span>
          </dt>
          <dd>
            <span v-text="t$('monolithApp.Language.' + jobHistory.language)"></span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.jobHistory.job')"></span>
          </dt>
          <dd>
            <div v-if="jobHistory.job">
              <router-link :to="{ name: 'MstJobView', params: { mstJobId: jobHistory.job.id } }">{{ jobHistory.job.id }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.jobHistory.department')"></span>
          </dt>
          <dd>
            <div v-if="jobHistory.department">
              <router-link :to="{ name: 'MstDepartmentView', params: { mstDepartmentId: jobHistory.department.id } }">{{
                jobHistory.department.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.jobHistory.employee')"></span>
          </dt>
          <dd>
            <div v-if="jobHistory.employee">
              <router-link :to="{ name: 'MstEmployeeView', params: { mstEmployeeId: jobHistory.employee.id } }">{{
                jobHistory.employee.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="jobHistory.id"
          :to="{ name: 'JobHistoryEdit', params: { jobHistoryId: jobHistory.id } }"
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

<script lang="ts" src="./job-history-details.component.ts"></script>
