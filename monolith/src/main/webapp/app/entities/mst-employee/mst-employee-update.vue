<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.mstEmployee.home.createOrEditLabel"
          data-cy="MstEmployeeCreateUpdateHeading"
          v-text="t$('monolithApp.mstEmployee.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="mstEmployee.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="mstEmployee.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstEmployee.firstName')" for="mst-employee-firstName"></label>
            <input
              type="text"
              class="form-control"
              name="firstName"
              id="mst-employee-firstName"
              data-cy="firstName"
              :class="{ valid: !v$.firstName.$invalid, invalid: v$.firstName.$invalid }"
              v-model="v$.firstName.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstEmployee.lastName')" for="mst-employee-lastName"></label>
            <input
              type="text"
              class="form-control"
              name="lastName"
              id="mst-employee-lastName"
              data-cy="lastName"
              :class="{ valid: !v$.lastName.$invalid, invalid: v$.lastName.$invalid }"
              v-model="v$.lastName.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstEmployee.email')" for="mst-employee-email"></label>
            <input
              type="text"
              class="form-control"
              name="email"
              id="mst-employee-email"
              data-cy="email"
              :class="{ valid: !v$.email.$invalid, invalid: v$.email.$invalid }"
              v-model="v$.email.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstEmployee.phoneNumber')" for="mst-employee-phoneNumber"></label>
            <input
              type="text"
              class="form-control"
              name="phoneNumber"
              id="mst-employee-phoneNumber"
              data-cy="phoneNumber"
              :class="{ valid: !v$.phoneNumber.$invalid, invalid: v$.phoneNumber.$invalid }"
              v-model="v$.phoneNumber.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstEmployee.hireDate')" for="mst-employee-hireDate"></label>
            <div class="d-flex">
              <input
                id="mst-employee-hireDate"
                data-cy="hireDate"
                type="datetime-local"
                class="form-control"
                name="hireDate"
                :class="{ valid: !v$.hireDate.$invalid, invalid: v$.hireDate.$invalid }"
                :value="convertDateTimeFromServer(v$.hireDate.$model)"
                @change="updateInstantField('hireDate', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstEmployee.salary')" for="mst-employee-salary"></label>
            <input
              type="number"
              class="form-control"
              name="salary"
              id="mst-employee-salary"
              data-cy="salary"
              :class="{ valid: !v$.salary.$invalid, invalid: v$.salary.$invalid }"
              v-model.number="v$.salary.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstEmployee.commissionPct')" for="mst-employee-commissionPct"></label>
            <input
              type="number"
              class="form-control"
              name="commissionPct"
              id="mst-employee-commissionPct"
              data-cy="commissionPct"
              :class="{ valid: !v$.commissionPct.$invalid, invalid: v$.commissionPct.$invalid }"
              v-model.number="v$.commissionPct.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstEmployee.manager')" for="mst-employee-manager"></label>
            <select class="form-control" id="mst-employee-manager" data-cy="manager" name="manager" v-model="mstEmployee.manager">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  mstEmployee.manager && mstEmployeeOption.id === mstEmployee.manager.id ? mstEmployee.manager : mstEmployeeOption
                "
                v-for="mstEmployeeOption in mstEmployees"
                :key="mstEmployeeOption.id"
              >
                {{ mstEmployeeOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstEmployee.department')" for="mst-employee-department"></label>
            <select
              class="form-control"
              id="mst-employee-department"
              data-cy="department"
              name="department"
              v-model="mstEmployee.department"
            >
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  mstEmployee.department && mstDepartmentOption.id === mstEmployee.department.id
                    ? mstEmployee.department
                    : mstDepartmentOption
                "
                v-for="mstDepartmentOption in mstDepartments"
                :key="mstDepartmentOption.id"
              >
                {{ mstDepartmentOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstEmployee.mstDepartment')" for="mst-employee-mstDepartment"></label>
            <select
              class="form-control"
              id="mst-employee-mstDepartment"
              data-cy="mstDepartment"
              name="mstDepartment"
              v-model="mstEmployee.mstDepartment"
            >
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  mstEmployee.mstDepartment && mstDepartmentOption.id === mstEmployee.mstDepartment.id
                    ? mstEmployee.mstDepartment
                    : mstDepartmentOption
                "
                v-for="mstDepartmentOption in mstDepartments"
                :key="mstDepartmentOption.id"
              >
                {{ mstDepartmentOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./mst-employee-update.component.ts"></script>
