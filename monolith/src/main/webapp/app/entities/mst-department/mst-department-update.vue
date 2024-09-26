<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.mstDepartment.home.createOrEditLabel"
          data-cy="MstDepartmentCreateUpdateHeading"
          v-text="t$('monolithApp.mstDepartment.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="mstDepartment.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="mstDepartment.id" readonly />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('monolithApp.mstDepartment.departmentName')"
              for="mst-department-departmentName"
            ></label>
            <input
              type="text"
              class="form-control"
              name="departmentName"
              id="mst-department-departmentName"
              data-cy="departmentName"
              :class="{ valid: !v$.departmentName.$invalid, invalid: v$.departmentName.$invalid }"
              v-model="v$.departmentName.$model"
              required
            />
            <div v-if="v$.departmentName.$anyDirty && v$.departmentName.$invalid">
              <small class="form-text text-danger" v-for="error of v$.departmentName.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstDepartment.location')" for="mst-department-location"></label>
            <select class="form-control" id="mst-department-location" data-cy="location" name="location" v-model="mstDepartment.location">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  mstDepartment.location && locationOption.id === mstDepartment.location.id ? mstDepartment.location : locationOption
                "
                v-for="locationOption in locations"
                :key="locationOption.id"
              >
                {{ locationOption.id }}
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
<script lang="ts" src="./mst-department-update.component.ts"></script>
