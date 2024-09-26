<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="mstJob">
        <h2 class="jh-entity-heading" data-cy="mstJobDetailsHeading">
          <span v-text="t$('monolithApp.mstJob.detail.title')"></span> {{ mstJob.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.mstJob.jobTitle')"></span>
          </dt>
          <dd>
            <span>{{ mstJob.jobTitle }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstJob.minSalary')"></span>
          </dt>
          <dd>
            <span>{{ mstJob.minSalary }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstJob.maxSalary')"></span>
          </dt>
          <dd>
            <span>{{ mstJob.maxSalary }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstJob.task')"></span>
          </dt>
          <dd>
            <span v-for="(task, i) in mstJob.tasks" :key="task.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'MstTaskView', params: { mstTaskId: task.id } }">{{ task.title }}</router-link>
            </span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstJob.employee')"></span>
          </dt>
          <dd>
            <div v-if="mstJob.employee">
              <router-link :to="{ name: 'MstEmployeeView', params: { mstEmployeeId: mstJob.employee.id } }">{{
                mstJob.employee.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="mstJob.id" :to="{ name: 'MstJobEdit', params: { mstJobId: mstJob.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./mst-job-details.component.ts"></script>
